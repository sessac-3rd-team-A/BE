package back.ahwhew.controller;

import back.ahwhew.dto.DiaryDTO;
import back.ahwhew.dto.ResultDTO;
import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.ResultRepository;
import back.ahwhew.service.DiaryService;
import back.ahwhew.service.UserService;
import back.ahwhew.service.resultService.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
//@Controller //타임리프 테스트용
@RestController
@RequestMapping("/api")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private DiaryService diaryService;


    @Autowired
    private UserService userService;
    //test용(동적폼전송으로..)
//    @GetMapping("/diary")
//    public String getDiaryPage() {
//        // GET 요청이 들어오면 diary.html 템플릿을 보여줌
//        return "diary";
//    }
    @Autowired
    private ResultRepository resultRepository;


    @PostMapping("/diary")
    @ResponseBody
    public ResponseEntity<ResultDTO> postTextDiary(@AuthenticationPrincipal UserEntity user, @RequestBody DiaryDTO diaryRequest) {

        try {
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


            log.info("check 경로 UserEntity : {}", String.valueOf(newUser));

            String textDiary = diaryRequest.getTextDiary();

//             텍스트 저장(나중에...)
           String saveDiary= diaryService.saveDiary(user,textDiary);
           log.info("saveDiary : {}",saveDiary);
//
//             클라이언트로부터 받은 일기 result Service에 넘겨서 서비스에 모든 로직 처리 후 필요한 값 반환

            ResultDTO resultDTO = resultService.getTextDiary(user, textDiary);

            return ResponseEntity.ok().body(resultDTO);


        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postTextDiary 메서드 실행 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<String> postPictureDiarySave(@AuthenticationPrincipal UserEntity user, @RequestBody ResultDTO resultDTO) {
        try {
            // ResultDTO를 ResultEntity로 변환
            ResultEntity resultEntity = ResultEntity.fromDTO(resultDTO);

            // 현재 로그인한 사용자의 UserEntity를 설정
            resultEntity.setUser(user);

            // ResultEntity를 저장
            String saveResult = resultService.save(resultEntity);

            // 저장이 성공하면 "success"를 반환
            return ResponseEntity.ok().body(saveResult);
        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postPictureDiarySave 메서드 실행 중 예외 발생", e);
            // 저장이 실패하면 "fail"을 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
        }
    }

}



