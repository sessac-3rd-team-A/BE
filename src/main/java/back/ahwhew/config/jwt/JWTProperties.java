package back.ahwhew.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 yml,properties 파일을 참고해서 가져와서 사용하는 어노테이션
public class JWTProperties {
    private String issuer;
    private String secretKey;
}
