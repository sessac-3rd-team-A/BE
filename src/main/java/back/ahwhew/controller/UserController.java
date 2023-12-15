package back.ahwhew.controller;

import back.ahwhew.dto.ResponseDTO;
import back.ahwhew.dto.UserDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService service;

//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO dto) {
        try{
            UserEntity user = UserEntity.builder()
                    .userId(dto.getUserId())
//                    .password(passwordEncoder.encode(dto.getPassword()))
                    .password(dto.getPassword())
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

    @GetMapping("/signup")
    public ResponseEntity<?> getNickname() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://nickname.hwanmoo.kr/?format=json&count=1";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 외부 API 응답 반환
        return ResponseEntity.ok().body(response.getBody());
    }
}
