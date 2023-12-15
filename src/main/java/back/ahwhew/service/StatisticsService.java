package back.ahwhew.service;

import back.ahwhew.entity.StatisticsEntity;
import back.ahwhew.repository.StatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StatisticsService {
    // 필드 주입
    @Autowired
    private  StatisticsRepository repository;

    // create statistics
    public List<StatisticsEntity> create(final StatisticsEntity entity){
    // read-only로 엔티티 설정 ㅇ_<
        repository.save(entity);
        log.info("Entity id: {} is saved. 통계데이터 추가 완료",entity.getId()); //통계데이터 생성시 로그

        return repository.findAll();
}
    // read statics -> 우선 전체 읽어오기
//    public List<StatisticsEntity> retrieveId(final String id) {
//        return repository.findById(id);
//    }

    public List<StatisticsEntity> retrieveAge(final String age) {
        return repository.findByAge(age);
    }

    public List<StatisticsEntity> retrieveGender(final char gender) {
        return repository.findByGender(gender);
    }

    public List<StatisticsEntity> retrieveDate(final String date) {
        return repository.findByDate(date);
    }
}