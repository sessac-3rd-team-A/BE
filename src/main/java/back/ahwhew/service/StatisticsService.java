package back.ahwhew.service;

import back.ahwhew.dto.AverageDTO;
import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.repository.StatisticsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StatisticsService {
    // 필드 주입
    @Autowired
    private  StatisticsRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // create statistics
    public List<StatisticsEntity> create(String result){
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
            entity.setRecommendedGif("임의값");

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

    public List<StatisticsEntity> getStatisticsByDate(Date date) {
        // 날짜별 통계를 얻기 위한 메서드
        return repository.findByDate(date);
    }

    public AverageDTO getOverallAverages() {
        List<StatisticsEntity> allData = repository.findAll();

        // 긍정, 부정, 중립값들의 평균을 계산
        double averagePositive = calculateAverage(allData, "positiveValue");
        double averageNegative = calculateAverage(allData, "negativeValue");
        double averageNeutral = calculateAverage(allData, "neutralValue");

        return new AverageDTO(averagePositive, averageNegative, averageNeutral);
    }

    public AverageDTO getAveragesByGender(char gender) {
        List<StatisticsEntity> genderData = repository.findByGender(gender);

        // 긍정, 부정, 중립값들의 평균을 계산
        double averagePositive = calculateAverage(genderData, "positiveValue");
        double averageNegative = calculateAverage(genderData, "negativeValue");
        double averageNeutral = calculateAverage(genderData, "neutralValue");

        return new AverageDTO(averagePositive, averageNegative, averageNeutral);
    }

    public AverageDTO getAveragesByAge(String age) {
        List<StatisticsEntity> ageData = repository.findByAge(age);

        // 긍정, 부정, 중립값들의 평균을 계산
        double averagePositive = calculateAverage(ageData, "positiveValue");
        double averageNegative = calculateAverage(ageData, "negativeValue");
        double averageNeutral = calculateAverage(ageData, "neutralValue");

        return new AverageDTO(averagePositive, averageNegative, averageNeutral);
    }

    private double calculateAverage(List<StatisticsEntity> data, String field) {
        double sum = 0.0;
        int count = 0;

        for (StatisticsEntity entity : data) {
            switch (field) {
                case "positiveValue":
                    sum += entity.getPositive();
                    break;
                case "negativeValue":
                    sum += entity.getNegative();
                    break;
                case "neutralValue":
                    sum += entity.getNeutral();
                    break;
            }
            count++;
        }

        return count > 0 ? sum / count : 0.0;
    }
    public AverageDTO getAveragesByGenderAndAge(char gender, String age) {
        List<StatisticsEntity> genderAndAgeData = repository.findByGenderAndAge(gender, age);

        // 긍정, 부정, 중립값들의 평균을 계산
        double averagePositive = calculateAverage(genderAndAgeData, "positiveValue");
        double averageNegative = calculateAverage(genderAndAgeData, "negativeValue");
        double averageNeutral = calculateAverage(genderAndAgeData, "neutralValue");

        return new AverageDTO(averagePositive, averageNegative, averageNeutral);
    }
}
