package back.ahwhew.controller;

import back.ahwhew.dto.ResponseDTO;
import back.ahwhew.dto.UserDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.security.TokenProvider;
import back.ahwhew.service.MypageService;
import back.ahwhew.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/mypage")
public class MypageController {
    @Autowired
    private MypageService mypageService;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/account")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserEntity userInfo, @RequestBody UserDTO dto) {
        try{
            log.info("Profile update start");

            log.info("확인: " + userInfo.getId());

            UserEntity user = UserEntity.builder()
                    .userId(dto.getUserId())
                    .age(dto.getAge())
                    .gender(dto.getGender())
                    .build();

            UserEntity registeredUser = mypageService.updateProfile(userInfo.getId(), user);

            // 토큰 재생성
            final String token = tokenProvider.create(user);

            final UserDTO resDTO = UserDTO.builder()
                    .userId(registeredUser.getUserId())
                    .age(registeredUser.getAge())
                    .gender(registeredUser.getGender())
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(resDTO);
        } catch(Exception e) {
            ResponseDTO resDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(resDTO);
        }
    }
}
