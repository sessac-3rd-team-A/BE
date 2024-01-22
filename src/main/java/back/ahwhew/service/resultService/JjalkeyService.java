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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class JjalkeyService {

    @Data
    public static class JjalkeyResponse {
        @JsonProperty("data")
        private List<JjalkeyData> data;
    }

    @Data
    public static class JjalkeyData {
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

    public String searchJjalkey(String query) {
        try {
            log.info("API 키는 :: {}", jjalkeyApiKey);

//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // 요청 URL 설정
//            String requestUrl = jjalkeyEndpoint;
//
//            // 요청 파라미터 설정
//            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(requestUrl)
//                    .queryParam("api_key", jjalkeyApiKey)
//                    .queryParam("q", query)
//                    .queryParam("grade", 4)
//                    .queryParam("limit", 1);
//
//
//            log.info("Jjalkey URL :: {}", uriBuilder.toUriString());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 URL 및 파라미터 수동 구성
            String requestUrl = jjalkeyEndpoint + "?api_key=" + jjalkeyApiKey + "&q=" + query + "&grade=4&limit=1";

            log.info("Jjalkey URL :: {}", requestUrl);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, String.class);
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답이 OK인 경우에만 JSON 매핑 시도
                log.info("Response :: {}", response);
                String responseBody = response.getBody();
                ObjectMapper objectMapper1 = new ObjectMapper();
                try {
                    // JSON 매핑 성공한 경우에는 정상 응답 반환
                    JjalkeyResponse jjalkeyResponse = objectMapper1.readValue(responseBody, JjalkeyResponse.class);

                    // URL 추출 및 사용
                    String jjalUrl=extractAndUseUrl(jjalkeyResponse);
                    return jjalUrl;

                } catch (Exception e) {
                    log.error("JSON 매핑 오류: {}", e.getMessage());
                    // JSON 매핑 실패 시에는 적절한 오류 응답을 반환
                    // 여기서는 빈 JjalkeyResponse 객체 반환
                    JjalkeyResponse errorResponse = new JjalkeyResponse();
                    errorResponse.setData(Collections.emptyList());
                    return null;
                }
            } else {
                log.error("Jjalkey API 검색 요청 실패. 응답코드::{}", response.getStatusCode());
                // 응답이 OK가 아닌 경우에는 적절한 오류 응답을 반환
                // 여기서는 빈 JjalkeyResponse 객체 반환
                JjalkeyResponse errorResponse = new JjalkeyResponse();
                errorResponse.setData(Collections.emptyList());
                return errorResponse.toString();
            }
        } catch (HttpClientErrorException e) {
            log.error("Jjalkey API 검색 요청 오류: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("Jjalkey API 검색 요청 오류: {}", e.getMessage());
        }
        return null;
    }
    private String extractAndUseUrl(JjalkeyResponse jjalkeyResponse) {
        // JjalkeyResponse 객체에서 데이터 리스트 가져오기
        List<JjalkeyData> dataList = jjalkeyResponse.getData();

        // 데이터 리스트가 비어있지 않고, 첫 번째 데이터가 존재하면 해당 데이터의 URL을 추출
        if (dataList != null && !dataList.isEmpty()) {
            JjalkeyData firstData = dataList.get(0);
            String imageUrl = firstData.getUrl();

            // imageUrl을 사용하거나 출력
            log.info("Extracted Image URL: {}", imageUrl);
            return imageUrl;
        } else {
            log.warn("No data found in JjalkeyResponse");
            return null;
        }
    }
}
