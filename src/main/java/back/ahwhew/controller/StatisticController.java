package back.ahwhew.controller;

import back.ahwhew.dto.AverageDTO;
import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.service.StatisticsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<AverageDTO> getAverages(@RequestParam(required = false) Character gender,
                                                  @RequestParam(required = false) String age) {

        AverageDTO result;

        if (gender != null && age != null) {
            result = statisticsService.getAveragesByGenderAndAge(gender, age);
        } else if (gender != null) {
            result = statisticsService.getAveragesByGender(gender);
        } else if (age != null) {
            result = statisticsService.getAveragesByAge(age);
        } else {
            // 카테고리 선택 안했으면 전체 유저 평균 데이터 전송
            result = statisticsService.getOverallAverages();
        }

        return ResponseEntity.ok(result);
    }
}
