package back.ahwhew.repository;

import back.ahwhew.entity.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<StatisticsEntity,Long> {
}
