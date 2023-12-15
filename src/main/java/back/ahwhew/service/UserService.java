package back.ahwhew.service;

import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository repo;

    public UserEntity create(final UserEntity userEntity) {
        final String userId = userEntity.getUserId();
        final String password = userEntity.getPassword();

        // user 정보 확인 - 필드 하나라도 비어있을 경우 확인
        if(userEntity == null) {
            throw new RuntimeException("Invalid arguments");
        }

        // 아이디
        if(userId == null || userId.trim().isEmpty()) {
            throw new RuntimeException("UserId is invalid arguments");
        }

        if(repo.existsByUserId(userId)) {
            log.warn("userId already exists {}", userId);
            throw new RuntimeException("Email already exists");
        }

        // 비밀번호
        if(password == null || password.trim().isEmpty() || SpecialCharacterCheck(password)) {
            throw new RuntimeException("Password is invalid arguments");
        }

        // 닉네임

        return repo.save(userEntity);
    }

//    // 비밀번호 암호화
//    public UserEntity getByCredentials(final String userId, final String password, final PasswordEncoder encoder) {
//        final UserEntity originalUser = repo.findByUserId(userId);
//
//        // matches() 메소드 이용해서 패스워드 동일 여부 비교
//        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
//            return originalUser;
//        }
//
//        return null;
//    }

    // 특수 문자 확인
    public Boolean SpecialCharacterCheck(String pw) {
        // 확인해야 할 특수문자
        String specialCharacterPattern = "[!@#$%^&*()\\\\?/.,]";

        Pattern pattern = Pattern.compile(specialCharacterPattern);
        Matcher matcher = pattern.matcher(pw);

        return matcher.find();
    }
}