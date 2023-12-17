package back.ahwhew.service.resultService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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
    private static final List<String> negativePrompt = Arrays.asList("text","letter", "signature", "watermark","string");
    public String getKarloResult(List<String> transferedWords) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "KakaoAK " + karloKey);

            RestTemplate restTemplate = new RestTemplate();

            // 요청 본문을 위한 맵 생성
            // 번역된 언어 이미지 생성을 위한 키워드 추가
//            transferedWords.add("cartoon");
            //cartoon을 프롬프트에 넣으니까 자꾸 텍스트가 추가되는 현상이 벌어짐
//            transferedWords.add("cute");
//            transferedWords.add("Draw a picture diary that matches this diary without text.");


            //요청 본문 만들기
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("prompt", String.join(",", transferedWords));
            requestBodyMap.put("negative_prompt", String.join(",", negativePrompt));
//            requestBodyMap.put("negative_prompt","text,letter,signature,watermark");
            requestBodyMap.put("width",600);
            requestBodyMap.put("height",600);
            requestBodyMap.put("image_format","png");
            requestBodyMap.put("guidance_scale",20);




            // 맵을 JSON으로 직렬화
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//            log.info("Karlo API에 보낼 요청 entity: {}", entity);

            // Karlo API 엔드포인트로 POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(
                    karloUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // 결과 처리 -> 이미지 주소 반환!
                return processKarloResult(response.getBody());
            } else {
                // API 요청이 실패한 경우
                log.error("Karlo API 요청 실패. 응답 코드: {}", response.getStatusCodeValue());
            }
        } catch (HttpClientErrorException e) {
            log.error("Karlo API 요청 오류: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException | JsonProcessingException e) {
            log.error("Karlo API 요청 오류: {}", e.getMessage());
        }
        return null;//오류발생시 null 반환
    }

    private String processKarloResult(String result) {
        // JSON 응답 파싱 및 결과 처리
//        log.info("Karlo API 결과: {}", result);
// Karlo API 응답을 기반으로 추가 처리 구현
// 이미지 저장
        try {
            if (result == null || result.isEmpty()) {
                log.info("Karlo API 결과가 없습니다.");
            }
            JSONObject karloResult = new JSONObject(result);
            // "images" 노드 추출
            JSONArray karloImageArray = karloResult.optJSONArray("images");
//            log.info("images: {}", karloImageArray);
            if (karloImageArray == null || karloImageArray.isEmpty()) {
                log.info("Karlo API Image 결과가 없습니다.");
            } else {
                // "images" 배열 순회
                for (int i = 0; i < karloImageArray.length(); i++) {
                    JSONObject karloImage = karloImageArray.getJSONObject(i);
                    // 각 이미지의 "image" 속성 값 추출
                    String imageUrl = karloImage.optString("image");
//                    log.info("Image URL {}: {}", i + 1, imageUrl);

                    return imageUrl; // 첫 번째 이미지 주소 반환 또는 필요한 작업 수행
                }
            }
        } catch (Exception e) {
            log.error("카를로 이미지 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}
