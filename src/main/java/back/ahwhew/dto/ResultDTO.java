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
    private String sentiment;
    private Timestamp date;

    public void ResultDTO(String userId, String pictureDiary, String sentiment, Timestamp date) {
        this.userId = userId;
        this.pictureDiary = pictureDiary;
        this.sentiment = sentiment;
        this.date = date;
    }
    public static ResultDTO toEntity(ResultEntity resultEntity) {
        return new ResultDTO(
                resultEntity.getId(),
                (resultEntity.getUserId() != null) ? resultEntity.getUserId().toString() : null,
                resultEntity.getPictureDiary(),
                resultEntity.getSentiment(),
                resultEntity.getDate()
        );
    }
}
