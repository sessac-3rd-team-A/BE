package back.ahwhew.service.resultService;

import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.repository.ResultRepository;
import back.ahwhew.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    NaverSentimentService naverSentimentService;

    @Autowired
    NaverPapagoService naverPapagoService;

    @Autowired
    KarloImageGeneratorService karloImageGeneratorService;

    @Autowired
    KarloImageEditService karloImageEditService;

    @Autowired
    AmazonS3Service amazonS3Service;

    @Autowired
    private StatisticsService statisticsService;
    public void getTextDiary(String textDiary) {
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

            // 통계값 저장
            List<StatisticsEntity> statisticsEntities = statisticsService.create(sentimentResult);

            //파파고 돌리기
            List<String> extractWords=naverSentimentService.extractWordsFromResult(sentimentResult);
            List<String> translatedText=naverPapagoService.translate(extractWords);
            log.info("translated result:: {}",translatedText);

            //karlo 돌리기(여기서는 base64로 인코딩된 값이 넘어옴
            String karloImgEncodedInfo= karloImageGeneratorService.getKarloResult(translatedText);
//            log.info("karlo result::{}",karloImgEncodedInfo);

            //karlo이미지 변환(Base64값)
            String editedImgInfo= karloImageEditService.changeImage(karloImgEncodedInfo,translatedText);
//            log.info("edited result::{}",editedImgInfo);

            //아마존S3에 이미지 업로드(업로드하고 url반환하는 함수)
            String imageUrl=amazonS3Service.uploadImageFromBase64(editedImgInfo);
            log.info("s3에 업로드한 imageUrl::{}",imageUrl);

        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("getTextDiary 메서드 실행 중 예외 발생", e);
        }
    }
}