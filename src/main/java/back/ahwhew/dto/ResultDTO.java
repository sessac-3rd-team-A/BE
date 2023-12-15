package back.ahwhew.dto;

import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultDTO {
    private Long id;
    private String userId;
    private String pictureDiary;
    private String recommendedGif;
    private String sentiment;
    private Double positive;
    private Double negative;
    private Double neutral;
    private Timestamp date;

    public void ResultDTO(String userId, String pictureDiary, String recommendedGif,String sentiment, Timestamp date,double positiveRatio, double negativeRatio, double neutralRatio) {
        this.userId = userId;
        this.pictureDiary = pictureDiary;
        this.recommendedGif=recommendedGif;
        this.sentiment = sentiment;
        this.date = date;
        this.positive=positiveRatio;
        this.negative=negativeRatio;
        this.neutral=neutralRatio;
    }
    public static ResultDTO toEntity(ResultEntity resultEntity) {
        return new ResultDTO(
                resultEntity.getId(),
                (resultEntity.getUserId() != null) ? resultEntity.getUserId().toString() : null,
                resultEntity.getPictureDiary(),
                resultEntity.getSentiment(),
                resultEntity.getRecommendedGif(),
                resultEntity.getPositiveRatio(),
                resultEntity.getNegativeRatio(),
                resultEntity.getNeutralRatio(),
                resultEntity.getDate()
        );
    }
}
