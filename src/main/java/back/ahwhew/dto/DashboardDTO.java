package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardDTO {

    private String date;
    private ResultDTO result;
//    private String yearMonth;
//    private List<MonthlyUserStatisticsDTO> monthlyStatistics;

}