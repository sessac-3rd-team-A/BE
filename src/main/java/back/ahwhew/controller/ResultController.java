package back.ahwhew.controller;

import back.ahwhew.dto.DiaryRequestDTO;
import back.ahwhew.service.resultService.NaverSentimentService;
import back.ahwhew.service.resultService.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class ResultController {

    @Autowired
    private ResultService resultService;
    //test용(동적폼전송으로..)
    @GetMapping("/diary")
    public String getDiaryPage() {
        // GET 요청이 들어오면 diary.html 템플릿을 보여줌
        return "diary";
    }

    @PostMapping("/diary")
    @ResponseBody
    public ResponseEntity<String> postTextDiary(@RequestBody DiaryRequestDTO diaryRequest) {
        try {
            String textDiary = diaryRequest.getTextDiary();
            // 클라이언트로부터 받은 일기 result Service에 넘겨서 서비스에 모든 로직 처리 후 필요한 값 반환
            resultService.getTextDiary(textDiary);


        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postTextDiary 메서드 실행 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
        return ResponseEntity.ok().body("Success");

    }

}
