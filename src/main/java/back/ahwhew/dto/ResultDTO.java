package back.ahwhew.dto;

import back.ahwhew.entity.ResultEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultDTO {
    private Long id;
    private String userId;
    private String pictureDiary;
    private List<String> recommendedGif; //유저가 선택하기 전 내보낼 gif는 여러개임
    private String sentiment;
    private Double positive;
    private Double negative;
    private Double neutral;
    private Timestamp date;

    public ResultDTO(String userId, String pictureDiary, List<String> recommendedGif, String sentiment, Timestamp date, double positiveRatio, double negativeRatio, double neutralRatio) {
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
                (resultEntity.getUserId() != null) ? resultEntity.getUserId().toString() : null,
                resultEntity.getPictureDiary(),
                Collections.singletonList(resultEntity.getRecommendedGif()), // 단일 URL을 리스트로 래핑
                resultEntity.getSentiment(),
                resultEntity.getDate(),
                resultEntity.getPositiveRatio(),
                resultEntity.getNegativeRatio(),
                resultEntity.getNeutralRatio()
        );
    }
}
