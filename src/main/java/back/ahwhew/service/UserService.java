package back.ahwhew.service;

import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository repository;

    public UserEntity getByCredentials(final String userId, final String password, final PasswordEncoder encoder){
        log.info("find user by userId");
        final UserEntity originalUser = repository.findByUserId(userId);
        log.info("original User: ", originalUser);
        // matches() 메소드 이용해서 패스워드 동일 여부 비교
        if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            log.info("samePassword");
            return originalUser;
        }

        log.info("wrongPassword");
        return null;
    }
}
