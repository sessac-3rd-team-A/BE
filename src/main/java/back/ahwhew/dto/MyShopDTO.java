package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyShopDTO {
    private String tag;
    private String sentiment;
    private String jobRelatedWords;
    private String jobCategories;
}
