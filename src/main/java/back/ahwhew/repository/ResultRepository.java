package back.ahwhew.repository;

import back.ahwhew.dto.ResultDTO;
import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ResultEntity r WHERE r.user.id = :userId AND r.date = :date")
    boolean existsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Query("SELECT r FROM ResultEntity r WHERE r.user = :user AND r.date = :now")
    ResultEntity findByUserAndDate(@Param("user") UserEntity user, @Param("now") LocalDate now);


    @Modifying
    @Transactional
    @Query("UPDATE ResultEntity SET sentiment = :sentiment, positiveRatio = :positiveRatio, negativeRatio = :negativeRatio, " +
            "neutralRatio = :neutralRatio, recommendedGif = :gifUrl, pictureDiary = :imageUrl WHERE user.id = :userId AND date = :date")
    void update(@Param("userId") UUID userId, @Param("sentiment") String sentiment, @Param("positiveRatio") double positiveRatio,
                @Param("negativeRatio") double negativeRatio, @Param("neutralRatio") double neutralRatio,
                @Param("gifUrl") String gifUrl, @Param("imageUrl") String imageUrl, @Param("date") LocalDate date);

    default ResultEntity saveOrUpdateResult(UserEntity user, String sentiment, double positiveRatio, double negativeRatio,
                                         double neutralRatio, String gifUrl, String imageUrl) {
        ResultEntity resultEntity = createResultEntity(user, sentiment, positiveRatio, negativeRatio, neutralRatio, gifUrl, imageUrl);

        if (user != null && user.getId() != null) {
            boolean exists = existsByUserIdAndDate(user.getId(), LocalDate.now(ZoneId.of("Asia/Seoul")));
            if (exists) {
                update(user.getId(), sentiment, positiveRatio, negativeRatio, neutralRatio, gifUrl, imageUrl, LocalDate.now(ZoneId.of("Asia/Seoul")));
                resultEntity = findByUserAndDate(user,LocalDate.now(ZoneId.of("Asia/Seoul"))); // 업데이트 후 엔터티를 조회하여 반환합니다..orElseThrow(); // 업데이트 후 엔터티를 조회하여 반환합니다.
            } else {
                resultEntity = save(resultEntity); // save 메서드를 호출하고 반환된 엔터티로 업데이트


            }
        } else {
            resultEntity = save(resultEntity); // save 메서드를 호출하고 반환된 엔터티로 업데이트
        }

        return resultEntity;
    }


    List<ResultEntity> findAllByUser(UserEntity user);

    Optional<ResultEntity> findById(Long id);

    private ResultEntity createResultEntity(UserEntity user, String sentiment, double positiveRatio, double negativeRatio,
                                            double neutralRatio, String gifUrl, String imageUrl) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setDate(LocalDate.now(ZoneId.of("Asia/Seoul")));

        if (user != null) {
            resultEntity.setUser(user);
        }

        resultEntity.setSentiment(sentiment);
        resultEntity.setPositiveRatio(positiveRatio);
        resultEntity.setNegativeRatio(negativeRatio);
        resultEntity.setNeutralRatio(neutralRatio);
        resultEntity.setPictureDiary(imageUrl);
        resultEntity.setRecommendedGif(gifUrl);

        return resultEntity;
    }

    ResultEntity findTopByUserIdOrderByDateDesc(UUID id);

}
