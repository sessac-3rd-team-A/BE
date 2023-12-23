package back.ahwhew.controller;

import back.ahwhew.dto.*;
import back.ahwhew.entity.DiaryEntity;
import back.ahwhew.entity.GifEntity;
import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.DiaryRepository;
import back.ahwhew.security.TokenProvider;
import back.ahwhew.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.HashMap;
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

    @Autowired
    DiaryService diaryService;

    @Autowired
    MyshopService myshopService;


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

    @GetMapping("/my-shop")
    public ResponseEntity<?> myShop(@AuthenticationPrincipal UserEntity userEntity) {
        try {
            // 상품 추천을 위해 고객 정보를 보내줌
            DiaryEntity latestDiaryEntity = myshopService.getLatestDiary(userEntity);
            log.info("최신 일기 정보::{}", latestDiaryEntity);

            ResultEntity latestResultEntity = myshopService.getLatestResult(userEntity);
            log.info("최신 결과 정보::{}", latestResultEntity);

            // MyShopDTO를 빌드하되, 최신 일기가 없는 경우 빈 객체로 생성
            MyShopDTO myshopDTO = MyShopDTO.builder()
                    .jobCategories(latestDiaryEntity != null ? latestDiaryEntity.getJobCategories() : null)
                    .jobRelatedWords(latestDiaryEntity != null ? latestDiaryEntity.getJobRelatedWords() : null)
                    .tag(latestResultEntity != null ? myshopService.getGifEntity(latestResultEntity.getRecommendedGif()).getTag() : null)
                    .sentiment(latestResultEntity != null ? latestResultEntity.getSentiment() : null)
                    .build();

            // 응답 메시지 설정
            Map<String, Object> response = new HashMap<>();
            response.put("userInfo", myshopDTO);

            if (latestDiaryEntity == null) {
                // 최근 일기가 없는 경우 메시지 추가
                Map<String, String> message = new HashMap<>();
                message.put("message", "최근 일기가 없습니다.");
                response.put("error", message);
                return ResponseEntity.ok().body(response);
            }

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
