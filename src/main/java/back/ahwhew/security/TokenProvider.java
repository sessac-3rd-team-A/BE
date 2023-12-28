package back.ahwhew.security;

import back.ahwhew.config.jwt.JWTProperties;
import back.ahwhew.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {

    @Autowired
    private JWTProperties jwtProperties;

    public String createAccessToken(UserEntity userEntity){
        log.info("creating access token");

        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        log.info("set access token expiryDate: {}", expiryDate);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512,jwtProperties.getSecretKey())
                .setSubject(String.valueOf(userEntity.getId()))
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("age", userEntity.getAge())
                .claim("gender", String.valueOf(userEntity.getGender()))
                .compact();
    }
    public String createRefreshToken(UserEntity userEntity){
        log.info("creating refresh token");

        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        log.info("set refresh token expiryDate: {}", expiryDate);

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512,jwtProperties.getSecretKey())
                .setSubject(String.valueOf(userEntity.getId()))
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("age", userEntity.getAge())
                .claim("gender", String.valueOf(userEntity.getGender()))
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        log.info("extract");
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Token expired date : {}", claims.getExpiration());
            log.info("date now: {}", Date.from(Instant.now()));

            return claims;

        }catch (ExpiredJwtException e){
            log.warn("ExpiredJwtException!!");
            Claims claims = Jwts.claims().setIssuer("Expired");

            return claims;

        }catch (Exception e) {
            log.warn("Exception : {}", e.getMessage());
            Claims claims = Jwts.claims().setIssuer("Token error");

            return claims;
        }
    }
}