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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/login")
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
}
