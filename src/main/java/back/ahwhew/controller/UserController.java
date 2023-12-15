package back.ahwhew.controller;

import back.ahwhew.dto.ResponseDTO;
import back.ahwhew.dto.UserDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.security.TokenProvider;
import back.ahwhew.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/signup")
    public ResponseEntity<?> getNickname() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://nickname.hwanmoo.kr/?format=json&count=1";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 외부 API 응답 반환
        return ResponseEntity.ok().body(response.getBody());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO dto) {
        try{
            log.info("Start signup");

            if(SpecialCharacterCheck(dto.getPassword())) {
                throw new RuntimeException("Password is invalid arguments");
            }

                UserEntity user = UserEntity.builder()
                        .userId(dto.getUserId())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .nickname(dto.getNickname())
                        .age(dto.getAge())
                        .gender(dto.getGender())
                        .build();

                UserEntity registeredUser = service.create(user);

                UserDTO resDTO = UserDTO.builder()
                        .userId(registeredUser.getUserId())
                        .password(registeredUser.getPassword())
                        .age(registeredUser.getAge())
                        .nickname(registeredUser.getNickname())
                        .gender(registeredUser.getGender())
                        .build();

                return ResponseEntity.ok().body(resDTO);
        } catch(Exception e) {
            ResponseDTO resDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(resDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO dto){
        log.info("Start signin");
        if (!isValidUser(dto)){
            ResponseDTO resDTO = ResponseDTO.builder()
                    .error("user valid fail")
                    .build();

            return ResponseEntity.badRequest().body(resDTO);
        }
        UserEntity user = service.getByCredentials(dto.getUserId(), dto.getPassword(), passwordEncoder);

        log.info("user: {}",user);
        if(user != null){
            log.info("user is not null");
            // 이메일, 비번으로 찾은 유저 있음 = 로그인 성공
            final String token = tokenProvider.create(user);
            log.info("finish creating token");
            final UserDTO resUserDTO = UserDTO.builder()
                    // 나중에 프론트와 연결시 필요한 요소 추가할것
                    .userId(user.getUserId())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .age(user.getAge())
                    .gender(user.getGender())
                    .token(token) // jwt 토큰 설정
                    .build();

            return ResponseEntity.ok().body(resUserDTO);

        } else {
            // userId, 비번으로 찾은 유저 없음 = 로그인 실패
            ResponseDTO resDTO = ResponseDTO.builder()
                    .error("Login failed")
                    .build();

            return ResponseEntity.badRequest().body(resDTO);
        }
    }

    private Boolean isValidUser(UserDTO userDTO){
        if(userDTO.getUserId() == null){
            log.warn("userId is null");
            return false;
        }else if(userDTO.getPassword() == null){
            log.warn("password is null");
            return false;
        }else {
            log.info("user vail checked");
            return true;
        }
    }

    // 특수 문자 확인
    public Boolean SpecialCharacterCheck(String pw) {
        // 확인해야 할 특수문자
        String specialCharacterPattern = "[!@#$%^&*()\\\\?/.,]";

        Pattern pattern = Pattern.compile(specialCharacterPattern);
        Matcher matcher = pattern.matcher(pw);

        return matcher.find();
    }
}