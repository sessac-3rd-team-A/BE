package back.ahwhew.controller;

import back.ahwhew.dto.*;
import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.security.TokenProvider;
import back.ahwhew.service.DashboardService;
import back.ahwhew.service.MypageService;
import back.ahwhew.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/profile")
public class MypageController {
    @Autowired
    private MypageService mypageService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    DashboardService dashbaordService;

    @PostMapping("/account")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserEntity userInfo, @RequestBody UserDTO dto) {
        try{
            log.info("Profile update start");

            log.info("확인: " + userInfo.getId());

            UserEntity user = UserEntity.builder()
                    .userId(dto.getUserId())
                    .age(dto.getAge())
                    .gender(dto.getGender())
                    .build();

            UserEntity registeredUser = mypageService.updateProfile(userInfo.getId(), user);

            // 토큰 재생성
            final String token = tokenProvider.create(user);

            final UserDTO resDTO = UserDTO.builder()
                    .userId(registeredUser.getUserId())
                    .age(registeredUser.getAge())
                    .gender(registeredUser.getGender())
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(resDTO);
        } catch(Exception e) {
            ResponseDTO resDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(resDTO);
        }
    }
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@AuthenticationPrincipal UserEntity userEntity) {
        try {
            log.info("Dashboard start");
            log.info("userEntity : {}", userEntity);
            if (userEntity == null)
                return ResponseEntity.badRequest().body("로그인 후 이용해주세요.");

            // 대시보드 정보 저장(유저 정보, 날짜 정보 등 저장해야함)
            List<ResultEntity> userResultList = dashbaordService.dashboard(userEntity);

            log.info("user의 dashboard list::{}", userResultList);
            log.info("user의 dashboard list 길이::{}", userResultList.size());

            TreeMap<String, DashboardDTO> resultMap = new TreeMap<>();


            for (ResultEntity result : userResultList) {
                ResultDTO resultDTO = new ResultDTO();
                resultDTO.setId(result.getId());
                resultDTO.setPictureDiary(result.getPictureDiary());
                resultDTO.setSentiment(result.getSentiment());
                resultDTO.setPositiveRatio(result.getPositiveRatio());
                resultDTO.setNegativeRatio(result.getNegativeRatio());
                resultDTO.setNeutralRatio(result.getNeutralRatio());
                resultDTO.setDate(result.getDate());
                resultDTO.setRecommendedGif(result.getRecommendedGif());

                DashboardDTO dashboardDTO = resultMap.computeIfAbsent(String.valueOf(result.getDate()), k -> DashboardDTO.builder().build());
                dashboardDTO.setResult(resultDTO);
                log.info("dashboardDTO::{}", dashboardDTO);

            }




            DashboardResDTO dashboardResDTO = DashboardResDTO.builder()
                    .calender(resultMap.entrySet()
                            .stream()
                            .map(entry -> {
                                DashboardDTO dashboardDTO = entry.getValue();
                                dashboardDTO.setDate(entry.getKey());
                                return dashboardDTO;
                            })
                            .collect(Collectors.toList()))
                    .monthlyStatistics(MonthlyUserStatisticsDTO.calculateMonthlyStatistics(
                            userResultList.stream()
                                    .map(result -> ResultDTO.builder()
                                            .positiveRatio(result.getPositiveRatio())
                                            .negativeRatio(result.getNegativeRatio())
                                            .neutralRatio(result.getNeutralRatio())
                                            .date(result.getDate())
                                            .build())
                                    .collect(Collectors.toList())))
                    .build();







            return ResponseEntity.ok().body(dashboardResDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
