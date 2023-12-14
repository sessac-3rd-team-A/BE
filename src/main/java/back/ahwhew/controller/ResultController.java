package back.ahwhew.controller;

import back.ahwhew.dto.DiaryRequestDTO;
import back.ahwhew.service.ResultService;
import lombok.extern.slf4j.Slf4j;
//import org.json.JSONArray;

//import org.json.JSONObject;

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

@Slf4j
@Controller
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Value("${naver-api.endpoint}")
    private String naverApiEndpoint;

    @Value("${naver-api.api-key}")
    private String naverApiKey;

    @Value("${naver-api.api-key-id}")
    private String naverApiKeyId;

    @PostMapping("/diary")
    @ResponseBody
    public void postTextDiary(@RequestBody DiaryRequestDTO diaryRequest) {
        try {
            String textDiary = diaryRequest.getTextDiary();
            // 클라이언트로부터 받은 일기 로깅
            log.info("클라이언트로부터 받은 일기: {}", textDiary);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-APIGW-API-KEY-ID", naverApiKeyId);
            headers.set("X-NCP-APIGW-API-KEY", naverApiKey);

            // 요청 본문 설정
            String requestBody = "{\"content\": \"" + textDiary + "\", \"config\": {\"negativeClassification\": true}}";

            // HttpEntity 생성
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

            // POST 요청 보내기
            String result = restTemplate.exchange(naverApiEndpoint, HttpMethod.POST, entity, String.class).getBody();

            // 감정 분석 결과 로깅
            log.info("감정 분석 결과: {}", result);

            // 추출된 단어 로깅
//            extractWordsFromResult(result);

        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postTextDiary 메서드 실행 중 예외 발생", e);
        }
    }

//    private void extractWordsFromResult(String result) {
//        try {
//            if (result == null) {
//                log.warn("감정 분석 결과가 null입니다.");
//                return;
//            }
//
//            // JSON 파싱
//            JSONObject jsonObject = new JSONObject(result);
//
//            // 'sentences' 노드 추출
//            JSONArray sentences = jsonObject.optJSONArray("sentences");
//
//             'sentences'가 null이 아닌지 확인
//            if (sentences == null || sentences.isEmpty()) {
//                log.warn("'sentences' 노드가 적절하게 포맷되지 않았습니다.");
//                return;
//            }
//
//            // 문장 반복 처리
//            for (int i = 0; i < sentences.length(); i++) {
//                JSONObject sentence = sentences.getJSONObject(i);
//
//                // 'content', 'offset', 'length' 노드 추출
//                String content = sentence.optString("content");
//                int offset = sentence.optInt("offset");
//                int length = sentence.optInt("length");
//
//                // 'offset' 및 'length'를 사용하여 단어 추출
//                String word = content.substring(offset, Math.min(offset + length, content.length()));
//
//                // 추출된 단어 출력 또는 필요한 작업 수행
//                log.info("단어 {}: {}", (i + 1), word);
//            }
//        } catch (Exception e) {
//            // 필요에 따라 예외 처리
//            log.error("결과에서 단어 추출 중 오류 발생", e);
//        }
//    }
}
