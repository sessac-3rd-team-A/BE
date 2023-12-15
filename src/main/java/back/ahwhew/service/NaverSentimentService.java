package back.ahwhew.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class NaverSentimentService {
    @Value("${naver-api.endpoint}")
    private String naverApiEndpoint;

    @Value("${naver-api.api-key}")
    private String naverApiKey;

    @Value("${naver-api.api-key-id}")
    private String naverApiKeyId;

    public void getSentiment (String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            //요청 헤더 설정
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-APIGW-API-KEY-ID", naverApiKeyId);
            headers.set("X-NCP-APIGW-API-KEY", naverApiKey);
            //요청 본문 설정
            String requestBody = "{\"content\": \"" + text + "\", \"config\": {\"negativeClassification\": true}}";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();

            String result = restTemplate.exchange(naverApiEndpoint, HttpMethod.POST, entity, String.class).getBody();

            log.info("감정 분석 결과: {}", result);

            //단어 추출
            List<String> extractWords = extractWordsFromResult(result);

            //db에 User의(null일수도 있음) date 별 sentiment(대표감정), positive, negative, neutral 비율 resultDTO 만들기.
            //resultDTO 일단 만들고 그 다음 짤 생성되고, 그림일기 생성 된 다음에 이미지 주소값 DTO에 추가해서 저장하기
            String sentiment=extractSentiment(result);
            log.info("naverSentimentService로 저장할 대표 감정->{}",sentiment);
            Double positiveRatio=extractPositiveRatio(result);
            log.info("positiveRatio::{}",positiveRatio);
            Double negativeRatio=extractNegativeRatio(result);
            log.info("negativeRatio::{}",negativeRatio);
            Double neutralRatio=extractNeutralRatio(result);
            log.info("neutralRatio::{}",neutralRatio);

        } catch (Exception e) {
            log.error("naverSentiment AI 감정 분석 중 에러 발생!{}", e);

        }

    }

    private List<String> extractWordsFromResult(String result) {
        List<String> extractedWords = new ArrayList<>();

        try {
            if (result == null || result.isEmpty()) {
                log.warn("감정 분석 결과가 null이거나 비어 있습니다.");
                return extractedWords;
            }

            // JSON 파싱
            JSONObject jsonObject = new JSONObject(result);

            // 'sentences' 노드 추출
            JSONArray sentencesArray = jsonObject.optJSONArray("sentences");
            log.info("sentencesArray: {}", sentencesArray);
            log.info("sentencesArray: {}", sentencesArray.length());
            // 'sentences'가 null이 아닌지 확인
            if (sentencesArray == null || sentencesArray.isEmpty()) {
                log.warn("'sentences' 노드가 적절하게 포맷되지 않았거나 비어 있습니다.");
                return extractedWords;
            }

            // 문장 반복 처리
            for (int i = 0; i < sentencesArray.length(); i++) {
                JSONObject sentence = sentencesArray.getJSONObject(i);
                log.info("sentence::{}", sentence);
                // 'content' 노드 추출
                String content = sentence.optString("content");

                JSONArray highlightsArray = sentence.optJSONArray("highlights");

                // 'highlights'가 null이 아닌지 확인
                if (highlightsArray != null && !highlightsArray.isEmpty()) {
                    // 각 highlight에 대한 정보를 출력 또는 필요한 작업 수행
                    for (int j = 0; j < highlightsArray.length(); j++) {
                        JSONObject highlight = highlightsArray.getJSONObject(j);
                        int highlightOffset = highlight.optInt("offset");
                        int highlightLength = highlight.optInt("length");

                        // 'offset' 및 'length'를 사용하여 강조된 단어 추출
                        String highlightedWord = content.substring(
                                highlightOffset,
                                Math.min(highlightOffset + highlightLength, content.length())
                        );

                        // 추출된 강조 단어를 List에 추가
                        extractedWords.add(highlightedWord);
                    }
                }
            }
        } catch (Exception e) {
            // 필요에 따라 예외 처리
            log.error("결과에서 단어 추출 중 오류 발생", e);
        }

        // List에 저장된 추출된 단어 반환
        return extractedWords;
    }
    private String extractSentiment(String result) {
        String sentiment = null;
        try {
            JSONObject jsonObject = new JSONObject(result);

            // "document" 객체 추출
            JSONObject document = jsonObject.optJSONObject("document");
            log.info("document: {}", document);
            // "sentiment" 키값 추출
            if (document != null) {
                sentiment = document.optString("sentiment");
                log.info("sentiment: {}", sentiment);
            }

            return sentiment;
        } catch (Exception e) {
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return sentiment;
    }
    private double extractPositiveRatio(String result) {
        double positiveRatio = 0.0;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONObject document = jsonObject.optJSONObject("document");
            if (document != null) {
                JSONObject confidence = document.optJSONObject("confidence");
                if (confidence != null) {
                    positiveRatio = confidence.optDouble("positive");
                    log.info("positiveRatio: {}", positiveRatio);
                    return positiveRatio;
                }
            }
        }catch(Exception e){
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return positiveRatio;
    }
    private double extractNegativeRatio(String result) {
        double negativeRatio = 0.0;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONObject document = jsonObject.optJSONObject("document");
            if (document != null) {
                JSONObject confidence = document.optJSONObject("confidence");
                if (confidence != null) {
                    negativeRatio = confidence.optDouble("negative");
                    log.info("negativeRatio: {}", negativeRatio);
                    return negativeRatio;
                }
            }
        }catch(Exception e){
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return negativeRatio;
    }
    private double extractNeutralRatio(String result) {
        double neutralRatio = 0.0;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONObject document = jsonObject.optJSONObject("document");
            if (document != null) {
                JSONObject confidence = document.optJSONObject("confidence");
                if (confidence != null) {
                    neutralRatio = confidence.optDouble("neutral");
                    log.info("neutralRatio: {}", neutralRatio);
                    return neutralRatio;
                }
            }
        }catch(Exception e){
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return neutralRatio;
    }

}

