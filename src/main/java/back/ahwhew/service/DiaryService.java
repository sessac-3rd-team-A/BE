package back.ahwhew.service;

import back.ahwhew.entity.DiaryEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.DiaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private KomoranService komoranService;

    public String saveDiary(UserEntity user, String text) {
        try {
            UUID userId = (user != null && user.getId() != null) ? user.getId() : null;
            UserEntity newUser = null;

            if (userId != null) {
                newUser = userService.getById(userId);
                log.info("UserEntity: {}", String.valueOf(newUser));
            } else {
                // userId가 null인 경우 처리
                log.warn("User ID is null");
            }

            List<String> jobRelatedWords = komoranService.extractJobRelatedWords(text);
            List<String> jobCategories = komoranService.extractJobCategories(text);
            log.info("직종 관련단어들-직종 카테고리::{}-{}", jobRelatedWords, jobCategories);

            diaryRepository.saveOrUpdate(newUser, text, jobRelatedWords, jobCategories);

            return "success";
        } catch (Exception e) {
            log.error("Error saving diary:", e);
            return "fail";
        }
    }


}

