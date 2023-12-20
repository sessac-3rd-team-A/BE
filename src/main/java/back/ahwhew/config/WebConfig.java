package back.ahwhew.config;

import back.ahwhew.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/diary")
//                .allowedOrigins("*")
//                .allowedMethods("GET", "POST")
//                .allowCredentials(false)
//                .maxAge(3600);
//    }

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean // 이 메소드가 생성하는 객체를 스프링이 관리
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 객체를 이용해서 http 요청에 대한 보안 설정
        http
                .cors(withDefaults()) // CORS 활성화
                .csrf(CsrfConfigurer::disable) // CSRF 보호 비활성화
                .httpBasic(withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음 (STATELESS 설정)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/auth/**","/api/diary","/api/diary/save","/api/**").permitAll() // /, /auth/** 경로는 모두 허용
                        .anyRequest().authenticated() // 그 외 나머지 경로에 대한 요청은 인증 필요
                );

        // 매 요청마다 CorsFilter 실행한 후에 jwtAuthenticationFilter 를 실행해달라고 등록
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // cors 설정
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해서 CORS 설정 적용

        return source;
    }
}
