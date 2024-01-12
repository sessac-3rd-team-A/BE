package back.ahwhew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyUserStatisticsDTO {
    private double positiveRatio;
    private double negativeRatio;
    private double neutralRatio;

    public static MonthlyUserStatisticsDTO calculateCurrentMonthStatistics(List<ResultDTO> resultDTOList) {
        // 현재 월을 기준으로 결과를 필터
        YearMonth currentMonth = YearMonth.now();
        List<ResultDTO> currentMonthResults = resultDTOList.stream()
                .filter(resultDTO -> YearMonth.from(resultDTO.getDate()).equals(currentMonth))
                .collect(Collectors.toList());

        // 현재 월에 대한 평균을 계산
        double positiveRatioAverage = calculateAverage(currentMonthResults, ResultDTO::getPositiveRatio);
        double negativeRatioAverage = calculateAverage(currentMonthResults, ResultDTO::getNegativeRatio);
        double neutralRatioAverage = calculateAverage(currentMonthResults, ResultDTO::getNeutralRatio);

        // 현재 월에 대한 MonthlyUserStatisticsDTO를 생성하고 반환
        return MonthlyUserStatisticsDTO.builder()
                .positiveRatio(positiveRatioAverage)
                .negativeRatio(negativeRatioAverage)
                .neutralRatio(neutralRatioAverage)
                .build();
    }
    private static double calculateAverage(List<ResultDTO> results, ToDoubleFunction<ResultDTO> mapper) {
        return results.stream()
                .mapToDouble(mapper)
                .average()
                .orElse(0.0);
    }
}
