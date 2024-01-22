package back.ahwhew.service.resultService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JjalkeyService {

    @Data
    public class JjalkeyResponse {
        @JsonProperty("data")
        private List<JjalkeyData> data;
    }

    @Data
    public class JjalkeyData {
        private String url;
        private String thumb;
        private String width;
        private String height;
        private String size;
    }

    @Value("${jjalkey.endpoint}")
    private String jjalkeyEndpoint;

    @Value("${jjalkey.apiKey}")
    private String jjalkeyApiKey;

    public JjalkeyResponse searchJjalkey(String query) {
        try {
            log.info("API 키는 :: {}", jjalkeyApiKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 파라미터 설정
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("api_key", jjalkeyApiKey);
            queryParams.put("q", query);
            queryParams.put("grade", 4);
            queryParams.put("limit", 1);
            queryParams.put("offset", 1);

            log.info("Jjalkey URL :: {}", jjalkeyEndpoint);

            // 맵을 JSON으로 직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            String queryString = objectMapper.writeValueAsString(queryParams);

            HttpEntity<String> entity = new HttpEntity<>(queryString, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(jjalkeyEndpoint, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답이 OK인 경우에만 JSON 매핑 시도
                log.info("Response :: {}", response);
                String responseBody = response.getBody();
                ObjectMapper objectMapper1 = new ObjectMapper();
                try {
                    // JSON 매핑 성공한 경우에는 정상 응답 반환
                    return objectMapper1.readValue(responseBody, JjalkeyResponse.class);
                } catch (JsonProcessingException e) {
                    log.error("JSON 매핑 오류: {}", e.getMessage());
                    // JSON 매핑 실패 시에는 적절한 오류 응답을 반환
                    // 여기서는 빈 JjalkeyResponse 객체 반환
                    JjalkeyResponse errorResponse = new JjalkeyResponse();
                    errorResponse.setData(Collections.emptyList());
                    return errorResponse;
                }
            } else {
                log.error("Jjalkey API 검색 요청 실패. 응답코드::{}", response.getStatusCode());
                // 응답이 OK가 아닌 경우에는 적절한 오류 응답을 반환
                // 여기서는 빈 JjalkeyResponse 객체 반환
                JjalkeyResponse errorResponse = new JjalkeyResponse();
                errorResponse.setData(Collections.emptyList());
                return errorResponse;
            }
        } catch (HttpClientErrorException e) {
            log.error("Jjalkey API 검색 요청 오류: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException | JsonProcessingException e) {
            log.error("Jjalkey API 검색 요청 오류: {}", e.getMessage());
        }
        return null;
    }
}
