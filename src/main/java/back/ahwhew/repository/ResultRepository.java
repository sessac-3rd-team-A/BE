package back.ahwhew.repository;

import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {

    default void save(UserEntity user, String sentiment, double positiveRatio, double negativeRatio, double neutralRatio, String gifUrl, String imageUrl) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setDate(LocalDate.now());


        if (user != null && user.getId() != null) {
            resultEntity.setUser(user);
        } else {
            resultEntity.setUser(null);
        }


        resultEntity.setSentiment(sentiment);
        resultEntity.setPositiveRatio(positiveRatio);
        resultEntity.setNegativeRatio(negativeRatio);
        resultEntity.setNeutralRatio(neutralRatio);
        resultEntity.setPictureDiary(imageUrl);
        resultEntity.setRecommendedGif(gifUrl);


        save(resultEntity);
    }

}
