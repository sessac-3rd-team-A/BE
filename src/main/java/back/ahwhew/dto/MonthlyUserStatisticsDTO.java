package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyUserStatisticsDTO {
    private double positiveRatio;
    private double negativeRatio;
    private double neutralRatio;

    // ResultDTO 리스트를 받아서 월별로 평균값을 계산하는 메서드
    public static Map<String, MonthlyUserStatisticsDTO> calculateMonthlyStatistics(List<ResultDTO> resultDTOList) {
        // 결과를 YearMonth를 키로 갖는 Map에 저장
        Map<YearMonth, List<ResultDTO>> resultByMonth = resultDTOList.stream()
                .collect(Collectors.groupingBy(resultDTO ->
                        YearMonth.from(resultDTO.getDate())));

        // 각 월별로 평균값을 계산하고 결과를 Map에 저장
        Map<String, MonthlyUserStatisticsDTO> monthlyStatisticsMap = new HashMap<>();
        for (Map.Entry<YearMonth, List<ResultDTO>> entry : resultByMonth.entrySet()) {
            YearMonth monthKey = entry.getKey();
            List<ResultDTO> filteredResults = entry.getValue();

            double positiveRatioAverage = filteredResults.stream()
                    .mapToDouble(ResultDTO::getPositiveRatio)
                    .average()
                    .orElse(0.0);

            double negativeRatioAverage = filteredResults.stream()
                    .mapToDouble(ResultDTO::getNegativeRatio)
                    .average()
                    .orElse(0.0);

            double neutralRatioAverage = filteredResults.stream()
                    .mapToDouble(ResultDTO::getNeutralRatio)
                    .average()
                    .orElse(0.0);

            MonthlyUserStatisticsDTO monthlyStatistics = MonthlyUserStatisticsDTO.builder()
                    .positiveRatio(positiveRatioAverage)
                    .negativeRatio(negativeRatioAverage)
                    .neutralRatio(neutralRatioAverage)
                    .build();

            monthlyStatisticsMap.put(monthKey.toString(), monthlyStatistics);
        }

        return monthlyStatisticsMap;
    }
}