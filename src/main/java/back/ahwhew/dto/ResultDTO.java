package back.ahwhew.dto;

import back.ahwhew.entity.ResultEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultDTO {
    private Long id;
    private UUID userId;
    private String pictureDiary;
    private String recommendedGif; //gif
    private String sentiment;
    private Double positive;
    private Double negative;
    private Double neutral;
    private LocalDate date;

    public ResultDTO(UUID userId, String pictureDiary, String recommendedGif, String sentiment, LocalDate date, double positiveRatio, double negativeRatio, double neutralRatio) {
        this.userId = userId;
        this.pictureDiary = pictureDiary;
        this.recommendedGif = recommendedGif;
        this.sentiment = sentiment;
        this.date = date;
        this.positive = positiveRatio;
        this.negative = negativeRatio;
        this.neutral = neutralRatio;
    }




    public static ResultDTO toEntity(ResultEntity resultEntity) {
        return new ResultDTO(
                (resultEntity.getUser() != null) ? resultEntity.getUser().getId() : null,
                resultEntity.getPictureDiary(),
                resultEntity.getRecommendedGif(),
                resultEntity.getSentiment(),
                resultEntity.getDate(),
                resultEntity.getPositiveRatio(),
                resultEntity.getNegativeRatio(),
                resultEntity.getNeutralRatio()
        );
    }
}
