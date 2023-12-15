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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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
}
