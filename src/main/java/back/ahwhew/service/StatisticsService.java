package back.ahwhew.service;

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

    public List<StatisticsEntity> retrieveAge(final String age) {
        return repository.findByAge(age);
    }

    public List<StatisticsEntity> retrieveGender(final char gender) {
        return repository.findByGender(gender);
    }

    public List<StatisticsEntity> retrieveDate(final Date date) {
        return repository.findByDate(date);
    }
}