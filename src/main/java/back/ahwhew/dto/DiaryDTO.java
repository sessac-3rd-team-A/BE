package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiaryDTO {
    private Long id;
    private String text;
    private String userId;
    private Timestamp date;

    // 생성자, 게터, 세터 등 필요한 메서드를 추가할 수 있습니다.

    public String getTextDiary() {
        return text;

    }

    public void setTextDiary(String textDiary) {
        this.text = textDiary;
    }
}
