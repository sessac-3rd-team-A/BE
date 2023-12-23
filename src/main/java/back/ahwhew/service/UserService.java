package back.ahwhew.service;

import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository repo;

    public UserEntity create(final UserEntity userEntity) {
        final String userId = userEntity.getUserId();
        final String password = userEntity.getPassword();
        final String nickname = userEntity.getNickname();
        final String age = userEntity.getAge();
        final char gender = userEntity.getGender();

        // user 정보 확인 - 필드 하나라도 비어있을 경우 확인
        if (userEntity == null) {
            throw new RuntimeException("Invalid arguments");
        }

        // 아이디
        if (userId == null || userId.trim().isEmpty()) {
            throw new RuntimeException("UserId is invalid arguments");
        }

        if (repo.existsByUserId(userId)) {
            log.warn("userId already exists {}", userId);
            throw new RuntimeException("UserId already exists");
        }

        // 비밀번호
        if (password == null) {
            log.info(password);
            throw new RuntimeException("Password is invalid arguments");
        }

        // 닉네임
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new RuntimeException("Nickname is invalid arguments");
        }

        // 나이
        if (age == null || age.trim().isEmpty()) {
            throw new RuntimeException("Age is invalid arguments");
        }

        // 성별
        if (gender != 'M' && gender != 'F') {
            throw new RuntimeException("Gender is invalid arguments");
        }

        return repo.save(userEntity);
    }

    public UserEntity getByCredentials(final String userId, final String password, final PasswordEncoder encoder){
        log.info("find user by userId");
        final UserEntity originalUser = repo.findByUserId(userId);
        log.info("original User: ", originalUser);
        // matches() 메소드 이용해서 패스워드 동일 여부 비교
        if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            log.info("samePassword");
            return originalUser;
        }else if(originalUser == null){
            log.info("wrong userId");
            UserEntity user = new UserEntity();
            user.setAge("wrong userId"); // age와 오류 메세지가 중복 가능섬이 없기 때문에 사용
            return user;
        }else if(!encoder.matches(password, originalUser.getPassword())){
            log.info("wrong password");
            UserEntity user = new UserEntity();
            user.setAge("wrong password");
            return user;
        }else{
            log.info("signin error");
            UserEntity user = new UserEntity();
            user.setAge("signin error");
            return user;
        }
    }

    public UserEntity getById(UUID id){
        UserEntity user = repo.findById(id);
        return user;
    }
}
