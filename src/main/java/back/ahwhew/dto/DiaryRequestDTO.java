package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiaryRequestDTO {
    private String textDiary;

    // 생성자, 게터, 세터 등 필요한 메서드를 추가할 수 있습니다.

    public String getTextDiary() {
        return textDiary;

    }

    public void setTextDiary(String textDiary) {
        this.textDiary = textDiary;
    }
}
