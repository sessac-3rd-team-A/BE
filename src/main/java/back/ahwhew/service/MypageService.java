package back.ahwhew.service;

import back.ahwhew.dto.UserDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Slf4j
public class MypageService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity updateProfile(String id,final UserEntity userEntity) {
        log.info("Profile update start (Service)");

        final String userId = userEntity.getUserId();
        final String age = userEntity.getAge();
        // gender는 문자열이 아니므로 필드가 비어있을 경우만 확인하면 됨

        final UserEntity user = userRepository.findById(UUID.fromString(id)); // 조건 문제

        log.info(String.valueOf(user.getId()));

        if (userEntity == null) {
            throw new RuntimeException("Invalid arguments");
        }

        if (userId == null || userId.trim().isEmpty()) {
            throw new RuntimeException("UserId is invalid arguments");
        }

        if (age == null || age.trim().isEmpty()) {
            throw new RuntimeException("Age is invalid arguments");
        }

        user.setUserId(userId);
        user.setAge(age);
        user.setGender(userEntity.getGender());

        return userRepository.save(user);
    }
}
