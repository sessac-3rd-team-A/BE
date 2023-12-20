package back.ahwhew.service;

import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.DiaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DiaryService {
    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    UserService userService;
    public String saveDiary(UserEntity user, String text){
        try{
            String userId = (user != null && user.getId() != null) ? user.getId().toString() : null;
            UserEntity newUser = null;
            Optional<UserEntity> optionalUser = null;
            if (userId != null) {
                optionalUser = Optional.ofNullable(userService.getById(UUID.fromString(userId)));
                newUser = optionalUser.orElse(null);
                log.info("check 경로 UserEntity: {}", String.valueOf(newUser));
            } else {
                // userId가 null인 경우 처리
                log.warn("User ID is null");
            }
            diaryRepository.save(user , text);
            return "success";
        }catch(Exception e){
            log.error("Error saving diary:", e);
            return "fail";
        }

    }
}
