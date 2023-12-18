package back.ahwhew.service.resultService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KarloImageEditService {
    @Value("${karlo.imgEditUrl}")
    private String karloImgChangeUrl;

    @Value("${karlo.key}")
    private String karloKey;

    @Autowired
    KarloImageGeneratorService karloImageGeneratorService;


    public String changeImage(String base64EncodedImgInfo, List<String> translatedWords) {
        try{
            HttpHeaders headers= new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "KakaoAK " + karloKey);

            RestTemplate restTemplate =new RestTemplate();

            //요청 본문 설정
            Map<String, Object>requestBodyMap=new HashMap<>();
            requestBodyMap.put("image",base64EncodedImgInfo);
            requestBodyMap.put("mask",base64EncodedImgInfo);
            requestBodyMap.put("prompt",String.join(",",translatedWords));
            requestBodyMap.put("negative_prompt","text,letter,signature,watermark");
            requestBodyMap.put("image_format","jpeg");
            requestBodyMap.put("return_type","base64_string");



            //맵을 JSON으로 직렬화
            ObjectMapper objectMapper=new ObjectMapper();
            String requestBody=objectMapper.writeValueAsString(requestBodyMap);

            HttpEntity<String> entity=new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response=restTemplate.exchange(karloImgChangeUrl, HttpMethod.POST,entity,String.class);

            if(response.getStatusCode()==HttpStatus.OK){
                return karloImageGeneratorService.processKarloResult(response.getBody());
            }else{
                log.error("Karlo 이미지 변환 요청 실패. 응답코드::{}",response.getStatusCode());
            }

        }catch(HttpClientErrorException e){
            log.error("Karlo 이미지 편집 요청 오류: {} - {}",e.getRawStatusCode(),e.getResponseBodyAsString());

        }catch(RestClientException | JsonProcessingException e){
            log.error("Karlo 이미지 편집 요청 오류: {}",e.getMessage());
        }
        return null; //오류발생시 null 반환 처리 필요. 예외처리 필요.
    }
}
