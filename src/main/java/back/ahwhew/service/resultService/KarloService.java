package back.ahwhew.service.resultService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KarloService {

    @Value("${karlo.url}")
    private String karloUrl;

    @Value("${karlo.key}")
    private String karloKey;

    public void getKarloResult(List<String> transferedWords) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", karloKey);

            RestTemplate restTemplate = new RestTemplate();

            // Jackson ObjectMapper를 사용하여 JSON 요청 본문 생성
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<String>> requestBodyMap = new HashMap<>();
            requestBodyMap.put("prompt", transferedWords);
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            log.info("karlo API에 보낼 요청 entit::{}", entity);

            // Karlo API 엔드포인트로 POST 요청 보내기
            String result = restTemplate.postForObject(karloUrl, entity, String.class);

            // 결과 처리
            processKarloResult(result);
        } catch (JsonProcessingException e) {
            log.error("JSON 처리 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Karlo API 요청 오류: {}", e.getMessage());
        }
    }

    private void processKarloResult(String result) {
        // JSON 응답 파싱 및 결과 처리
        log.info("Karlo API 결과: {}", result);
        // Karlo API 응답을 기반으로 추가 처리 구현
        // 예를 들어, 결과 저장 또는 추가 작업 수행
    }
}
