package back.ahwhew.controller;

import back.ahwhew.dto.ResponseDTO;
import back.ahwhew.dto.UserDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.security.TokenProvider;
import back.ahwhew.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

            // 유저 유효성 검사
            String validCheck = isValidUser(dto);
            if (!validCheck.equals("checked")){
                ResponseDTO resDTO = ResponseDTO.builder()
                        .error(validCheck)
                        .build();

                return ResponseEntity.badRequest().body(resDTO);
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

    @GetMapping("/check")
    public ResponseEntity<?> check(@AuthenticationPrincipal UserEntity user, HttpServletRequest request){
        // @AuthenticationPrincipal UserEntity user 사용시 주의 사항
        // user에는 id, age, gender만 들어가있음. 이외에는 service.getById(user.getId());를 활용해 user값을 가져와야함
        log.info("UserEntity age from 어노테이션 : {}", user.getAge());
        log.info("UserEntity Gender from 어노테이션 : {}", user.getGender());

        // 요청에 토큰 담아서 보냈을때 User정보 가져오는 코드
        log.info("check 경로 id : {}", user.getId());
        UserEntity newUser = service.getById(user.getId());
        log.info("check 경로 UserEntity : {}", String.valueOf(newUser));
        return ResponseEntity.ok().body(String.valueOf(newUser));
    }


    private String isValidUser(UserDTO userDTO){

        if(userDTO.getUserId() == null || userDTO.getUserId().isEmpty()){ //userId가 null이거나 빈 값일때
            log.warn("userId is null or empty");
            return "userId is null or empty";
        }else if(userDTO.getPassword() == null || userDTO.getPassword().isEmpty()){ //password가 null이거나 빈 값일때
            log.warn("password is null or empty");
            return "password is null or empty";
        }else {
            log.info("user valid checked");
            return "checked";
        }
    }


}