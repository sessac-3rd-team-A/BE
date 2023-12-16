package back.ahwhew.service.resultService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            headers.set("Authorization", "KakaoAK " + karloKey);

            RestTemplate restTemplate = new RestTemplate();

            // 문자열로 이어서 JSON 요청 본문 생성
            String requestBody = "{\"prompt\": \"" + String.join(",", transferedWords) + "\"}";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            log.info("Karlo API에 보낼 요청 entity: {}", entity);

            // Karlo API 엔드포인트로 POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(
                    karloUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // 결과 처리
                processKarloResult(response.getBody());
            } else {
                // API 요청이 실패한 경우
                log.error("Karlo API 요청 실패. 응답 코드: {}", response.getStatusCodeValue());
            }
        } catch (HttpClientErrorException e) {
            log.error("Karlo API 요청 오류: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
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
