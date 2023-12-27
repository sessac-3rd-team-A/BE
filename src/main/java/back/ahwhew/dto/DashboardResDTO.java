package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DashboardResDTO {
    private String error;
    private List<DashboardDTO> calendar;

}
