package back.ahwhew.service.resultService;

import back.ahwhew.entity.ResultEntity;
import back.ahwhew.repository.ResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NaverSentimentService {
    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private NaverPapagoService naverPapagoService;

    @Value("${naver-api.endpoint}")
    private String naverApiEndpoint;

    @Value("${naver-api.api-key}")
    private String naverApiKey;

    @Value("${naver-api.api-key-id}")
    private String naverApiKeyId;

    public String getSentiment(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            //요청 헤더 설정
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-APIGW-API-KEY-ID", naverApiKeyId);
            headers.set("X-NCP-APIGW-API-KEY", naverApiKey);
            //요청 본문 설정
            // 요청 본문 설정
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("content", text);

            // config 설정
            Map<String, Object> requestConfigMap = new HashMap<>();
            requestConfigMap.put("negativeClassification", true);

            // config를 requestBodyMap에 추가
            requestBodyMap.put("config", requestConfigMap);

            // Map을 JSON으로 변환
            ObjectMapper objectMapper = new ObjectMapper();

            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();

            String result = restTemplate.exchange(naverApiEndpoint, HttpMethod.POST, entity, String.class).getBody();

            log.info("감정 분석 결과: {}", result);


            return result;

        } catch (Exception e) {
            log.error("naverSentiment AI 감정 분석 중 에러 발생! {}", e);
            return null; // 오류 발생 시 null 반환
        }
    }

    private HttpHeaders createRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // 요청 헤더 설정
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", naverApiKeyId);
        headers.set("X-NCP-APIGW-API-KEY", naverApiKey);
        return headers;
    }


    public List<String> extractWordsFromResult(String result) {
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

            // 'sentences'가 null이 아닌지 확인
            if (sentencesArray == null || sentencesArray.isEmpty()) {
                log.warn("'sentences' 노드가 적절하게 포맷되지 않았거나 비어 있습니다.");
                return extractedWords;
            }

            // 문장 반복 처리
            for (int i = 0; i < sentencesArray.length(); i++) {
                JSONObject sentence = sentencesArray.getJSONObject(i);

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
            log.error("결과에서 단어 추출 중 오류 발생", e);
        }

        // 공백을 기준으로 자르고 중복 제거
        return extractedWords;
    }

    public String extractSentiment(String result) {
        String sentiment = null;
        try {
            JSONObject jsonObject = new JSONObject(result);

            // "document" 객체 추출
            JSONObject document = jsonObject.optJSONObject("document");

            // "sentiment" 키값 추출
            if (document != null) {
                sentiment = document.optString("sentiment");
            }

            return sentiment;
        } catch (Exception e) {
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return sentiment;
    }

    public double extractPositiveRatio(String result) {
        double positiveRatio = 0.0;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject document = jsonObject.optJSONObject("document");
            if (document != null) {
                JSONObject confidence = document.optJSONObject("confidence");
                if (confidence != null) {
                    positiveRatio = confidence.optDouble("positive");
                }
            }
        } catch (Exception e) {
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return positiveRatio;
    }

    public double extractNegativeRatio(String result) {
        double negativeRatio = 0.0;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject document = jsonObject.optJSONObject("document");
            if (document != null) {
                JSONObject confidence = document.optJSONObject("confidence");
                if (confidence != null) {
                    negativeRatio = confidence.optDouble("negative");
                }
            }
        } catch (Exception e) {
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return negativeRatio;
    }

    public double extractNeutralRatio(String result) {
        double neutralRatio = 0.0;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject document = jsonObject.optJSONObject("document");
            if (document != null) {
                JSONObject confidence = document.optJSONObject("confidence");
                if (confidence != null) {
                    neutralRatio = confidence.optDouble("neutral");
                }
            }
        } catch (Exception e) {
            log.error("결과에서 감정 추출 중 오류 발생", e);
        }
        return neutralRatio;
    }
    public String extractDetailNegativeSentiment(String result) {
        JSONObject jsonObject = new JSONObject(result);
        JSONObject document = jsonObject.optJSONObject("document");

        // document 객체가 null인 경우 예외 처리
        if (document == null) {
            log.error("document 객체가 null입니다. 감정 추출에 실패했습니다.");
            return null;
        }

        JSONArray sentences = jsonObject.optJSONArray("sentences");

        // sentences 배열이 null이거나 비어있는 경우 예외 처리
        if (sentences != null && sentences.length() > 0) {
            for (int i = 0; i < sentences.length(); i++) {
                JSONObject sentence = sentences.getJSONObject(i);
                JSONObject negativeSentiment = sentence.optJSONObject("negativeSentiment");

                // negativeSentiment 객체가 null이 아닌 경우에만 처리
                if (negativeSentiment != null) {
                    String detailedNegativeSentiment = negativeSentiment.optString("sentiment");
                    return detailedNegativeSentiment;
                }
            }
        }

        return null; // 부정 감정이 나타나지 않는 경우
    }



}
