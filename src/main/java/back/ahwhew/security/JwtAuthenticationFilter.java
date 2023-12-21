package back.ahwhew.security;

import back.ahwhew.entity.UserEntity;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter
    // - 한 요청당 한 번 실행됨

    @Autowired
    private TokenProvider tokenProvider;

    // doFilterInternal 메소드
    // - OncePerREquestFilter 에 정의된 추상 메소드 중 하나
    // - 재정의한 메소드에서 하는 작업: JWT 토큰 검증, 사용자 정보를 Spring Security 의 SecurityContextHolder에 등록

    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.info("Filter is running...");
            String token = parseBearerToken(request);

            // token 검사
            // - 토큰 인증 부분 구현
            // - 유효시간 검사 생략
            if(token != null && !token.equalsIgnoreCase("null")){ //equalsIgnoreCase 대소문자 상관 안하고 비교
                // 토큰이 null, "null"이 아니라면 토큰 검사 진행

                // user의 id 가져오기
                // - 만약 토큰이 위조되었다면 예외 처리
//                String id = tokenProvider.validateAndGetId(token);
//                log.info(("Authenticated user ID: " + id));

                Claims claims = tokenProvider.extractClaims(token);
                log.info("claims : {}", claims);

                UserEntity user = new UserEntity();
                user.setId(UUID.fromString(claims.getSubject()));
                user.setAge(claims.get("age",String.class));
                user.setGender(claims.get("gender", String.class).charAt(0));

                // 인증 완료 -> SecurityContextHolder 에 등록 되어야 인증된 사용자!
                AbstractAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.NO_AUTHORITIES); // 사용자 정보
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 사용자 인증 세부 정보 설정

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); /// 빈 SecurityContext 생성
                securityContext.setAuthentication(authentication); // context에 인증 정보 설정
                SecurityContextHolder.setContext(securityContext); // SecurityContextHolder 저장
            }else{
                log.warn("Token is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // 응답 메시지 작성
                response.getWriter().write("토큰이 null이거나 유효하지 않습니다.");

                return;
            }
        }catch (Exception e){
            logger.error("Could not set user authentication in security context", e);
        }

        // 다음 필터로 계속 진행
        filterChain.doFilter(request, response);

    }

    private String parseBearerToken(HttpServletRequest request){
        // 요청의 헤더에서 Bearer 토큰을 가져옴
        String bearerToken = request.getHeader("Authorization");

        // 토큰 파싱
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // Bearer 6글자 + 공백 1글자
        }

        return null;
    }
}