package back.ahwhew.controller;

import back.ahwhew.dto.DiaryRequestDTO;
import back.ahwhew.dto.ResultDTO;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.service.UserService;
import back.ahwhew.service.resultService.NaverSentimentService;
import back.ahwhew.service.resultService.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
//@Controller //타임리프 테스트용
@RestController
@RequestMapping("/api")
public class ResultController {

    @Autowired
    private ResultService resultService;
    @Autowired
    private UserService userService;
    //test용(동적폼전송으로..)
//    @GetMapping("/diary")
//    public String getDiaryPage() {
//        // GET 요청이 들어오면 diary.html 템플릿을 보여줌
//        return "diary";
//    }

    @PostMapping("/diary")
    @ResponseBody
    public ResponseEntity<ResultDTO> postTextDiary(@AuthenticationPrincipal UserEntity user, @RequestBody DiaryRequestDTO diaryRequest) {
        try {
            Optional<UserEntity> optionalUser = Optional.ofNullable(userService.getById(user.getId()));
            UserEntity newUser = optionalUser.orElse(null); // null을 기본값으로 사용
            //null인 경우에도 동작하게 하려고 함


            log.info("check 경로 UserEntity : {}", String.valueOf(newUser));

            String textDiary = diaryRequest.getTextDiary();
            // 클라이언트로부터 받은 일기 result Service에 넘겨서 서비스에 모든 로직 처리 후 필요한 값 반환
            ResultDTO resultDTO = resultService.getTextDiary(newUser,textDiary);

            return ResponseEntity.ok().body(resultDTO);

        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postTextDiary 메서드 실행 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
