package back.ahwhew.controller;

import back.ahwhew.dto.AverageDTO;
import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.service.StatisticsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api")
public class StatisticController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<AverageDTO> getAverages(@RequestParam(required = false) Character gender,
                                                  @RequestParam(required = false) String age) {
        AverageDTO result = null;

        try {
            log.info("get/api/statistics 실행 ");

            if (gender != null && age != null) {
                log.info("params 값 : {},{}", gender, age);
                result = statisticsService.getAveragesByGenderAndAge(gender, age);
            } else if (gender != null) {
                result = statisticsService.getAveragesByGender(gender);
            } else if (age != null) {
                result = statisticsService.getAveragesByAge(age);
            } else {
                // 카테고리 선택 안했으면 전체 유저 평균 데이터 전송
                result = statisticsService.getOverallAverages();
            }

            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage(), e);
            // 예외에 대한 적절한 응답을 클라이언트에게 반환하거나, 기본값이나 빈 결과를 반환하는 등의 처리를 수행할 수 있습니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
