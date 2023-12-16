package back.ahwhew.service.resultService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NaverPapagoService {
    @Value("${naver-papago.endpoint}")
    private String naverPapagoEndpoint;

    @Value("${naver-papago.api-key-id}")
    private String naverPapagoApiKey;

    @Value("${naver-papago.api-key}")
    private String naverPapagoApiSecret;

    @Autowired
    KarloService karloService;

    public List<String> transfer(List<String> extractsWords) {
        List<String> translatedResults = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            // 요청 헤더 설정
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-NCP-APIGW-API-KEY", naverPapagoApiSecret);
            headers.set("X-NCP-APIGW-API-KEY-ID", naverPapagoApiKey);

            RestTemplate restTemplate = new RestTemplate();

            // 각 단어를 번역하여 로그에 출력
            for (String word : extractsWords) {
                // 요청 본문 설정
                String requestBody = "{\"source\": \"ko\", \"target\": \"en\", \"text\": \"" + word + "\"}";

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                // 번역 서비스 호출
                String result = restTemplate.postForObject(naverPapagoEndpoint, entity, String.class);

                log.info("Word: {}, Translated Result: {}", word, result);

                // JSON 파싱하여 "translatedText" 값 가져오기
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(result);
                String translatedText = rootNode.path("message").path("result").path("translatedText").asText();

                // 리스트에 번역 결과 추가
                translatedResults.add(translatedText);
            }

        } catch (Exception e) {
            log.error("네이버 파파고 오류: {}", e.getMessage());
        }
        log.info("Translated Results: {}", translatedResults);
        karloService.getKarloResult(translatedResults);
        return translatedResults;
    }
}