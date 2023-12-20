package back.ahwhew.repository;

import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ResultEntity r WHERE r.user.id = :userId AND r.date = :date")
    boolean existsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);


    @Modifying
    @Transactional
    @Query("UPDATE ResultEntity SET sentiment = :sentiment ,positiveRatio=:positiveRatio ,negativeRatio=:negativeRatio ,neutralRatio=:neutralRatio, recommendedGif=:gifUrl, pictureDiary=:imageUrl WHERE user.id = :userId AND date = :date ")
    void update(@Param("userId") UUID userId, @Param("sentiment") String sentiment,@Param("positiveRatio") double positiveRatio,@Param("negativeRatio") double negativeRatio,@Param("neutralRatio") double neutralRatio,@Param("gifUrl") String gifUrl,@Param("imageUrl") String imageUrl,@Param("date") LocalDate date);
    default void save(UserEntity user, String sentiment, double positiveRatio, double negativeRatio, double neutralRatio, String gifUrl, String imageUrl) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setDate(LocalDate.now());




        if (user != null && user.getId() != null) {//로그인
            resultEntity.setUser(user);
            boolean exists = existsByUserIdAndDate(user.getId(), LocalDate.now());
            if (!exists) {//해당 날짜의 유저의 그림일기가 존재하지 않으면 생성
                resultEntity.setSentiment(sentiment);
                resultEntity.setPositiveRatio(positiveRatio);
                resultEntity.setNegativeRatio(negativeRatio);
                resultEntity.setNeutralRatio(neutralRatio);
                resultEntity.setPictureDiary(imageUrl);
                resultEntity.setRecommendedGif(gifUrl);

                save(resultEntity);
            }else{
                update(user.getId(),sentiment,positiveRatio,negativeRatio,neutralRatio,gifUrl,imageUrl,LocalDate.now());
            }
        } else { //비로그인
            resultEntity.setUser(null);
            resultEntity.setSentiment(sentiment);
            resultEntity.setPositiveRatio(positiveRatio);
            resultEntity.setNegativeRatio(negativeRatio);
            resultEntity.setNeutralRatio(neutralRatio);
            resultEntity.setPictureDiary(imageUrl);
            resultEntity.setRecommendedGif(gifUrl);


            save(resultEntity);
        }

//        user.addResult(resultEntity);
    }

    List<ResultEntity> findAllByUser(UserEntity user);

}
