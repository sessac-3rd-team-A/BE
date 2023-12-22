package back.ahwhew.repository;

import back.ahwhew.entity.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity,Long> {
    // 테이블에 매핑될 엔티티 클래스는 statisticsEntity고 PK는 Long 타입
    StatisticsEntity findById(long id);
    List<StatisticsEntity> findAll();

    List<StatisticsEntity> findByGenderAndAge(char gender, String age);

    List<StatisticsEntity> findByAge(String age);
    List<StatisticsEntity> findByGender(char gender);

    List<StatisticsEntity> findByDate(LocalDate date);

    List<StatisticsEntity> findAllByDateBetween(LocalDate startDate,LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM ResultEntity r " +
            "WHERE r.user.id = :userId AND r.date = :date")
    boolean existsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);

}

