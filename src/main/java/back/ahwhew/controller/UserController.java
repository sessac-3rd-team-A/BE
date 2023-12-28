package back.ahwhew.controller;

import back.ahwhew.config.jwt.JWTProperties;
import back.ahwhew.dto.ResponseDTO;
import back.ahwhew.dto.UserDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.security.TokenProvider;
import back.ahwhew.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTProperties jwtProperties;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO dto) {
        try{
            log.info("Start signup");

            // 닉네임 api 호출
            WebClient webClient = WebClient.create();
            String apiUrl = "https://nickname.hwanmoo.kr/?format=json&count=1";

            String response = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);

            // "words" 키에 해당하는 값을 추출
            JsonNode wordsNode = jsonNode.get("words");

            // JsonNode를 String으로 변환
            String firstWord = wordsNode.get(0).asText();

            log.info("nickname: " + firstWord);

            // 유저 유효성 검사
            String validCheck = isValidUser(dto);
            if (!validCheck.equals("checked")){
                return ResponseEntity.badRequest().body(false);
            }

            UserEntity user = UserEntity.builder()
                    .userId(dto.getUserId())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .nickname(firstWord)
                    .age(dto.getAge())
                    .gender(dto.getGender())
                    .build();

            service.create(user);

            return ResponseEntity.ok().body(true);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(false);
        }

    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO dto){
        log.info("Start signin");

        // 유저 유효성 검사
        String validCheck = isValidUser(dto);
        if (!validCheck.equals("checked")){
            ResponseDTO resDTO = ResponseDTO.builder()
                    .error(validCheck)
                    .build();

            return ResponseEntity.badRequest().body(resDTO);
        }

        UserEntity user = service.getByCredentials(dto.getUserId(), dto.getPassword(), passwordEncoder);
        log.info("user: {}",user);

        if(user.getUserId() != null){
            log.info("user is not null");
            // userId, password로 찾은 유저 있음 = 로그인 성공
            final String accessToken = tokenProvider.createAccessToken(user);
            final String refreshToken = tokenProvider.createRefreshToken(user);
            log.info("accessToken value: {}", accessToken);
            log.info("finish creating token");

            final UserDTO resUserDTO = UserDTO.builder()
                    .userId(user.getUserId())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .age(user.getAge())
                    .gender(user.getGender())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok().body(resUserDTO);

        } else {
            // userId, password로 찾은 유저 없음 = 로그인 실패
            ResponseDTO resDTO = ResponseDTO.builder()
                    .error(user.getAge()) // service에서 로그인 실패 사유를 age에 담아 보내기 때문
                    .build();

            return ResponseEntity.status(401).body(resDTO);
        }
    }

    // accessToken 재발급
    @PostMapping("/newToken")
    public ResponseEntity<?> createNewToken(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        log.info("create new accessToken from : {}", token);

        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();

        UUID id = UUID.fromString(claims.getSubject());
        log.info("id : {}", id);

        UserEntity user = service.getById(id);
        String accessToken = tokenProvider.createAccessToken(user);
        final UserDTO resUserDTO = UserDTO.builder()
                .userId(user.getUserId())
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok().body(resUserDTO);
    }

    // refreshToken 재발급
    @PostMapping("/newRefreshToken")
    public ResponseEntity<?> createNewRefreshToken(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        log.info("create new refresh Token from : {}", token);

        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();

        UUID id = UUID.fromString(claims.getSubject());
        log.info("id : {}", id);

        UserEntity user = service.getById(id);
        String refreshToken = tokenProvider.createRefreshToken(user);
        final UserDTO resUserDTO = UserDTO.builder()
                .userId(user.getUserId())
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok().body(resUserDTO);
    }

    private String isValidUser(UserDTO userDTO){
        if(userDTO.getUserId() == null || userDTO.getUserId().isEmpty()){ //userId가 null이거나 빈 값일때
            log.warn("userId is null or empty");
            return "userId is null or empty";
        }else if(userDTO.getPassword() == null || userDTO.getPassword().isEmpty()){ //password가 null이거나 빈 값일때
            log.warn("password is null or empty");
            return "password is null or empty";
        }else if(userDTO.getPassword().length() < 4 || userDTO.getPassword().length() > 12) {
            log.warn("password is too long or short");
            return "password is too long or short";
        }else if(userDTO.getUserId().length() < 4 || userDTO.getUserId().length() > 12) {
            log.warn("userId is too long or short");
            return "userId is too long or short";
        }
        else {
            log.info("user valid checked");
            return "checked";
        }
    }
}