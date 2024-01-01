package back.ahwhew.controller;

import back.ahwhew.dto.DiaryDTO;
import back.ahwhew.dto.ResponseDTO;
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

    @Autowired
    private ResultRepository resultRepository;
    @GetMapping("/diary/{resultId}")
    public ResponseEntity<ResultDTO> getDiaryPage(@PathVariable Long resultId) {
        log.info("resultId : {}", resultId);
        ResultEntity resultEntity = resultRepository.findById(resultId).orElse(null);
        if (resultEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ResultDTO resultDTO= ResultDTO.builder()
                .id(resultEntity.getId())
                .sentiment(resultEntity.getSentiment())
                .positiveRatio(resultEntity.getPositiveRatio())
                .negativeRatio(resultEntity.getNegativeRatio())
                .neutralRatio(resultEntity.getNeutralRatio())
                .date(resultEntity.getDate())
                .recommendedGif(resultEntity.getRecommendedGif())
                .pictureDiary(resultEntity.getPictureDiary())
                .build();
        return ResponseEntity.ok(resultDTO);
    }


    @PostMapping("/diary")
    @ResponseBody
    public ResponseEntity<ResultDTO> postTextDiary(@AuthenticationPrincipal UserEntity user, @RequestBody DiaryDTO diaryRequest) {

        try {
            String textDiary = diaryRequest.getTextDiary();
            //로그인한 유저의 경우에만 ㅈ장
            String saveDiary= diaryService.saveDiary(user,textDiary);
            log.info("saveDiary : {}",saveDiary);
            //서비스에서 ai로직 처리
            ResultDTO resultDTO = resultService.getTextDiary(user, textDiary);

            return ResponseEntity.ok().body(resultDTO);


        } catch (Exception e) {
            // 예외 발생 시 로깅
            log.error("postTextDiary 메서드 실행 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}



