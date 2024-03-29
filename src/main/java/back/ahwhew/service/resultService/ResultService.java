package back.ahwhew.service.resultService;

import back.ahwhew.dto.GifDTO;
import back.ahwhew.dto.ResultDTO;
import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.ResultRepository;
import back.ahwhew.service.KomoranService;
import back.ahwhew.service.StatisticsService;
import back.ahwhew.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private JjalkeyService jjalkeyService;
    @Autowired
    private NaverSentimentService naverSentimentService;

    @Autowired
    private NaverPapagoService naverPapagoService;

    @Autowired
    private KarloImageGeneratorService karloImageGeneratorService;

    @Autowired
    private KarloImageEditService karloImageEditService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private ClassifyTagService classifyTagService;

    @Autowired
    private GifService gifService;

    @Autowired
    private UserService userService;

    @Autowired
    private KomoranService komoranService;

    @Autowired
    private StatisticsService statisticsService;


    public  ResultDTO getTextDiary(UserEntity user, String textDiary) {
        try {
            //센티멘트 전체 결과값
            String sentimentResult = naverSentimentService.getSentiment(textDiary);
            //대표 감정 추출
            String sentiment = naverSentimentService.extractSentiment(sentimentResult);
            log.info("sentiment:: {}", sentiment);
            // extractPositiveRatio 함수 호출-> 긍정 감정 비율
            double positiveRatio = naverSentimentService.extractPositiveRatio(sentimentResult);
            log.info("positiveRatio:: {}", positiveRatio);
            // extractNegativeRatio 함수 호출
            double negativeRatio = naverSentimentService.extractNegativeRatio(sentimentResult);
            log.info("negativeRatio:: {}",negativeRatio);
            // extractNeutralRatio 함수 호출->부정 감정 비율
            double neutralRatio = naverSentimentService.extractNeutralRatio(sentimentResult);
            log.info("neutralRatio:: {}", neutralRatio);

            //부정감정인지 아닌지 판단 후 세부감정 추출
            String detailNegativeSentiment=naverSentimentService.extractDetailNegativeSentiment(sentimentResult);
            log.info("detailNegativeSentiment:: {}",detailNegativeSentiment);

//            //Gif 태그값 지정
//            String classifyTag= classifyTagService.classifySentiment(sentiment,positiveRatio,negativeRatio,neutralRatio,detailNegativeSentiment);
//            log.info("지정된 태그값:: {}",classifyTag);
//
//            //지정된 태그값으로 사진 가져오기
//            String gifUrl=gifService.getRandomGifUrl(classifyTag);
//            log.info("imageUrls:: {}",gifUrl);







            List<String> extractWords = naverSentimentService.extractWordsFromResult(sentimentResult);
            log.info("extractWords:: {}", extractWords);

            List<String> translatedText = null;
            String jjalUrl = null;
            String jjalQuery;

            if (extractWords.size() == 1 && extractWords.get(0).length() <= 10) {
                // 길이가 10 이하인 경우 Papago 번역기 사용
                jjalQuery = extractWords.get(0);
                log.info("짤 검색어:: {}", jjalQuery);
            } else {
                // 길이가 10 초과인 경우 KomoranService 사용
                List<String> phrasesWithNegations = komoranService.extractNounPhrases(extractWords);

                log.info("phrases with negations:: {}", phrasesWithNegations);
                jjalQuery = phrasesWithNegations.get(0);
                log.info("짤 검색어:: {}", jjalQuery);
            }

            // jjalQuery에 공백이 있는 경우 처리
            if (jjalQuery.contains(" ")) {
                // 공백을 기준으로 나누어서 필요한 부분 사용
                String[] splitWords = jjalQuery.split("\\s+");
                if (splitWords.length > 1) {

                    jjalQuery = splitWords[0];
                }
            }
            log.info("jjalQuery::{}",jjalQuery);

            jjalUrl = jjalkeyService.searchJjalkey(jjalQuery);
            log.info("jjalkey :: {}", jjalUrl);
            // 통계값 저장
            List<StatisticsEntity> statisticsEntities = statisticsService.create(user,sentimentResult,jjalUrl);
            //파파고 번역기 돌리기
            translatedText = naverPapagoService.translate(extractWords);

            //karlo 돌리기(여기서는 base64로 인코딩된 값이 넘어옴
            String karloImgEncodedInfo= karloImageGeneratorService.getKarloResult(translatedText);
//            log.info("karlo result::{}",karloImgEncodedInfo);//이거 주석 풀면 엄청 긴 인코딩 된 암호 나오니까 안푸는거 추천함

            //karlo이미지 변환(Base64값)
            String editedImgInfo= karloImageEditService.changeImage(karloImgEncodedInfo,translatedText);
//            log.info("edited result::{}",editedImgInfo);//이거 주석 풀면 엄청 긴 인코딩 된 암호 나오니까 안푸는거 추천함

            //아마존S3에 이미지 업로드(업로드하고 url반환하는 함수)

            String imageUrl=amazonS3Service.uploadImageFromBase64(editedImgInfo);
            log.info("s3에 업로드한 imageUrl::{}",imageUrl);

            ResultEntity resultEntity=resultRepository.saveOrUpdateResult(user,sentiment,positiveRatio,negativeRatio,neutralRatio,jjalUrl,imageUrl);
            ResultDTO resultDTO= ResultDTO.builder()
                    .id(resultEntity.getId())
                    .userId(resultEntity.getUser() != null ? resultEntity.getUser().getId() : null)
                    .sentiment(resultEntity.getSentiment())
                    .positiveRatio(resultEntity.getPositiveRatio())
                    .negativeRatio(resultEntity.getNegativeRatio())
                    .neutralRatio(resultEntity.getNeutralRatio())
                    .date(resultEntity.getDate())
                    .recommendedGif(resultEntity.getRecommendedGif())
                    .pictureDiary(resultEntity.getPictureDiary())
                    .build();

            return resultDTO;


        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("getTextDiary 메서드 실행 중 예외 발생", e);
            throw new RuntimeException("Failed to process text diary", e);
        }
    }
}