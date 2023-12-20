package back.ahwhew.service;

import back.ahwhew.dto.AverageDTO;
import back.ahwhew.dto.GifDTO;
import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.StatisticsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticsService {
    // 필드 주입
    @Autowired
    private  StatisticsRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // create statistics
    public List<StatisticsEntity> create(UserEntity user,String result,String gifUrl){
        // result 값 파싱해서 entity에 넣고 저장
        List<StatisticsEntity> statisticsEntities = new ArrayList<>();

        try{
            JsonNode rootNode = objectMapper.readTree(result);
            JsonNode documentNode = rootNode.path("document");
            JsonNode confidenceNode = documentNode.path("confidence");

            double negative = confidenceNode.path("negative").asDouble();
            double positive = confidenceNode.path("positive").asDouble();
            double neutral = confidenceNode.path("neutral").asDouble();

            StatisticsEntity entity =  new StatisticsEntity();
            entity.setNegative(negative);
            entity.setPositive(positive);
            entity.setNeutral(neutral);

            if (user != null) {
                entity.setAge(user.getAge());
                entity.setGender(user.getGender());
            } else {
                //유저가 비로그인시 Null
                entity.setAge(null);
                entity.setGender(null);
            }
            entity.setRecommendedGif(gifUrl);

            repository.save(entity);
            log.info("Entity id: {} is saved. 통계데이터 추가 완료", entity.getId()); // 통계데이터 생성시 로그
            statisticsEntities = repository.findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return statisticsEntities;
    }

    public List<StatisticsEntity> getOverallStatistics() {
        // 전체 통계를 얻기 위한 메서드 (카테고리 선택 안한 경우)
        return repository.findAll();
    }

    public List<StatisticsEntity> getStatisticsByGenderAndAge(char gender, String age) {
        // 성별과 연령별 통계를 얻기 위한 메서드
        return repository.findByGenderAndAge(gender, age);
    }

    public List<StatisticsEntity> getStatisticsByGender(char gender) {
        // 성별과 연령별 통계를 얻기 위한 메서드
        return repository.findByGender(gender);
    }

    public List<StatisticsEntity> getStatisticsByAge(String age) {
        // 성별과 연령별 통계를 얻기 위한 메서드
        return repository.findByAge(age);
    }

    public List<StatisticsEntity> getStatisticsByDate(LocalDate date) {
        // 날짜별 통계를 얻기 위한 메서드
        return repository.findByDate(date);
    }

    public  List<AverageDTO> getOverallAverages(LocalDate startDate,LocalDate endDate) {
        try {

            List<StatisticsEntity> allDateInRange = repository.findAllByDateBetween(startDate,endDate);

            List<StatisticsEntity> filteredData = allDateInRange.stream()
                    .filter(entity -> {
                        LocalDate entityDate = entity.getDate();
                        return entityDate != null &&
                                (entityDate.isEqual(startDate) || (entityDate.isAfter(startDate) && !entityDate.isAfter(endDate.plusDays(1))));
                    })
                    .collect(Collectors.toList());

            List<AverageDTO> dailyAverages = calculateAverage(filteredData);


            return dailyAverages.isEmpty() ? Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0)) : dailyAverages;
        } catch (RuntimeException e) {
            log.error("에러 발생: {}", e.getMessage(), e);
            // 예외 처리. 기본값 반환
            return Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0));
        }
    }

    public List<AverageDTO> getAveragesByGender(char gender,LocalDate startDate, LocalDate endDate) {
        try {
            List<StatisticsEntity> genderData = repository.findByGender(gender);

            List<StatisticsEntity> filteredData = genderData.stream()
                    .filter(entity -> {
                        LocalDate entityDate = entity.getDate();
                        return entityDate != null &&
                                (entityDate.isEqual(startDate) || (entityDate.isAfter(startDate) && !entityDate.isAfter(endDate.plusDays(1))));
                    })
                    .collect(Collectors.toList());


            List<AverageDTO> dailyAverages = calculateAverage(filteredData);

            return dailyAverages.isEmpty() ? Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0)) : dailyAverages;
        } catch (RuntimeException e) {
            log.error("에러 발생: {}", e.getMessage(), e);
            // 예외 처리. 기본값 반환
            return Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0));
        }
    }


    public  List<AverageDTO> getAveragesByAge(String age,LocalDate startDate, LocalDate endDate) {
        try {
            List<StatisticsEntity> ageData = repository.findByAge(age);

            List<StatisticsEntity> filteredData = ageData.stream()
                    .filter(entity -> {
                        LocalDate entityDate = entity.getDate();
                        return entityDate != null &&
                                (entityDate.isEqual(startDate) || (entityDate.isAfter(startDate) && !entityDate.isAfter(endDate.plusDays(1))));
                    })
                    .collect(Collectors.toList());

            // 긍정, 부정, 중립값들의 평균을 계산
            List<AverageDTO> dailyAverages = calculateAverage(filteredData);

            return dailyAverages.isEmpty() ? Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0)) : dailyAverages;
        } catch (RuntimeException e) {
            log.error("에러 발생: {}", e.getMessage(), e);
            // 예외 처리. 기본값 반환
            return Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0));
        }
    }

    public  List<AverageDTO> getAveragesByGenderAndAge(char gender, String age,LocalDate startDate, LocalDate endDate) {
        try {
            log.info("평균값 계산 메서드 실행:c");
            List<StatisticsEntity> genderAndAgeData = repository.findByGenderAndAge(gender, age);

            List<StatisticsEntity> filteredData = genderAndAgeData.stream()
                    .filter(entity -> {
                        LocalDate entityDate = entity.getDate();
                        return entityDate != null &&
                                (entityDate.isEqual(startDate) || (entityDate.isAfter(startDate) && !entityDate.isAfter(endDate.plusDays(1))));
                    })
                    .collect(Collectors.toList());

            List<AverageDTO> dailyAverages = calculateAverage(filteredData);

            return dailyAverages.isEmpty() ? Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0)) : dailyAverages;
        } catch (RuntimeException e) {
            log.error("에러 발생: {}", e.getMessage(), e);
            // 예외 처리. 기본값 반환
            return Collections.singletonList(new AverageDTO(LocalDate.now(), 0.0, 0.0, 0.0));
        }
    }

    private List<AverageDTO> calculateAverage(List<StatisticsEntity> data){
        Map<LocalDate, AverageDTO> dailyAveragesMap = new LinkedHashMap<>();

        for (StatisticsEntity entity : data) {
            LocalDate date = entity.getDate();
            AverageDTO dailyAverage = dailyAveragesMap.getOrDefault(date, new AverageDTO(date, 0.0, 0.0, 0.0));

            dailyAverage.setAveragePositive((dailyAverage.getAveragePositive() + entity.getPositive()));
            dailyAverage.setAverageNegative((dailyAverage.getAverageNegative() + entity.getNegative()));
            dailyAverage.setAverageNeutral((dailyAverage.getAverageNeutral() + entity.getNeutral()));

            dailyAverage.incrementCount();
            dailyAveragesMap.put(date,dailyAverage);
        }

        for(AverageDTO dailyAverage: dailyAveragesMap.values()){
            int count = dailyAverage.getCount();

            log.info("count : {}",count);
            if(count>0){
                log.info("dailyAverage.getAveragePositive()는 : {}",dailyAverage.getAveragePositive());
                log.info("dailyAverage.getAveragePositive()에서 나누면  : {}",dailyAverage.getAveragePositive() / count);
                dailyAverage.setAveragePositive(dailyAverage.getAveragePositive() / count);
                dailyAverage.setAverageNegative(dailyAverage.getAverageNegative() / count);
                dailyAverage.setAverageNeutral(dailyAverage.getAverageNeutral() / count);
            }

        }
        return new ArrayList<>(dailyAveragesMap.values());
        }
//    private double calculateAverage(List<StatisticsEntity> data, String field) {
//        double sum = 0.0;
//        int count = 0;
//
//        for (StatisticsEntity entity : data) {
//            switch (field) {
//                case "positiveValue":
//                    sum += entity.getPositive();
//                    break;
//                case "negativeValue":
//                    sum += entity.getNegative();
//                    break;
//                case "neutralValue":
//                    sum += entity.getNeutral();
//                    break;
//            }
//            count++;
//        }
//
//        return count > 0 ? sum / count : 0.0;
//    }
}
