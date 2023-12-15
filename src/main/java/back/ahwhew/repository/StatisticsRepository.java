package back.ahwhew.repository;

import back.ahwhew.entity.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity,Long> {
    // 테이블에 매핑될 엔티티 클래스는 statisticsEntity고 PK는 Long 타입
    List<StatisticsEntity> findAll();
    List<StatisticsEntity> findByAge(String age);
    List<StatisticsEntity> findByGender(char gender);

}

