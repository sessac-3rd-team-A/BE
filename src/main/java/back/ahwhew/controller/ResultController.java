package back.ahwhew.controller;

import back.ahwhew.dto.DiaryRequestDTO;
import back.ahwhew.service.resultService.NaverSentimentService;
import back.ahwhew.service.resultService.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private NaverSentimentService naverSentimentService;

//    @Value("${naver-api.endpoint}")
//    private String naverApiEndpoint;
//
//    @Value("${naver-api.api-key}")
//    private String naverApiKey;
//
//    @Value("${naver-api.api-key-id}")
//    private String naverApiKeyId;

    @PostMapping("/diary")
    @ResponseBody
    public void postTextDiary(@RequestBody DiaryRequestDTO diaryRequest) {
        try {
            String textDiary = diaryRequest.getTextDiary();
            // 클라이언트로부터 받은 일기 로깅
            log.info("클라이언트로부터 받은 일기: {}", textDiary);
            //서비스에서 로직
            naverSentimentService.getSentiment(textDiary);
            // 헤더 설정
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("X-NCP-APIGW-API-KEY-ID", naverApiKeyId);
//            headers.set("X-NCP-APIGW-API-KEY", naverApiKey);

            // 요청 본문 설정
//            String requestBody = "{\"content\": \"" + textDiary + "\", \"config\": {\"negativeClassification\": true}}";

            // HttpEntity 생성
//            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // RestTemplate 생성
//            RestTemplate restTemplate = new RestTemplate();

            // POST 요청 보내기
//            String result = restTemplate.exchange(naverApiEndpoint, HttpMethod.POST, entity, String.class).getBody();

            // 감정 분석 결과 로깅
//            log.info("감정 분석 결과: {}", result);

            // 추출된 단어 로깅
//            List<String> extractWords = extractWordsFromResult(result);
//            log.info("추출된 단어 리스트!!: {}", extractWords);

        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postTextDiary 메서드 실행 중 예외 발생", e);
        }

    }

//    private List<String> extractWordsFromResult(String result) {
//        List<String> extractedWords = new ArrayList<>();
//
//        try {
//            if (result == null || result.isEmpty()) {
//                log.warn("감정 분석 결과가 null이거나 비어 있습니다.");
//                return extractedWords;
//            }
//
//            // JSON 파싱
//            JSONObject jsonObject = new JSONObject(result);
//
//            // 'sentences' 노드 추출
//            JSONArray sentencesArray = jsonObject.optJSONArray("sentences");
//            log.info("sentencesArray: {}", sentencesArray);
//            log.info("sentencesArray: {}", sentencesArray.length());
//            // 'sentences'가 null이 아닌지 확인
//            if (sentencesArray == null || sentencesArray.isEmpty()) {
//                log.warn("'sentences' 노드가 적절하게 포맷되지 않았거나 비어 있습니다.");
//                return extractedWords;
//            }
//
//            // 문장 반복 처리
//            for (int i = 0; i < sentencesArray.length(); i++) {
//                JSONObject sentence = sentencesArray.getJSONObject(i);
//                log.info("sentence::{}", sentence);
//                // 'content' 노드 추출
//                String content = sentence.optString("content");
//
//                JSONArray highlightsArray = sentence.optJSONArray("highlights");
//
//                // 'highlights'가 null이 아닌지 확인
//                if (highlightsArray != null && !highlightsArray.isEmpty()) {
//                    // 각 highlight에 대한 정보를 출력 또는 필요한 작업 수행
//                    for (int j = 0; j < highlightsArray.length(); j++) {
//                        JSONObject highlight = highlightsArray.getJSONObject(j);
//                        int highlightOffset = highlight.optInt("offset");
//                        int highlightLength = highlight.optInt("length");
//
//                        // 'offset' 및 'length'를 사용하여 강조된 단어 추출
//                        String highlightedWord = content.substring(
//                                highlightOffset,
//                                Math.min(highlightOffset + highlightLength, content.length())
//                        );
//
//                        // 추출된 강조 단어를 List에 추가
//                        extractedWords.add(highlightedWord);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            // 필요에 따라 예외 처리
//            log.error("결과에서 단어 추출 중 오류 발생", e);
//        }
//
//        // List에 저장된 추출된 단어 반환
//        return extractedWords;
//    }





}
