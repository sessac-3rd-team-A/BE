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
//    public static List<MonthlyUserStatisticsDTO> calculateMonthlyStatistics(List<ResultDTO> resultDTOList) {
//        // 결과를 YearMonth를 키로 갖는 Map에 저장
//        Map<YearMonth, List<ResultDTO>> resultByMonth = resultDTOList.stream()
//                .collect(Collectors.groupingBy(resultDTO ->
//                        YearMonth.from(resultDTO.getDate())));
//
//        // 각 월별로 평균값을 계산하고 결과를 List에 저장
//        List<MonthlyUserStatisticsDTO> monthlyStatisticsList = new ArrayList<>();
//        for (Map.Entry<YearMonth, List<ResultDTO>> entry : resultByMonth.entrySet()) {
//            YearMonth monthKey = entry.getKey();
//            List<ResultDTO> filteredResults = entry.getValue();
//
//            double positiveRatioAverage = filteredResults.stream()
//                    .mapToDouble(ResultDTO::getPositiveRatio)
//                    .average()
//                    .orElse(0.0);
//
//            double negativeRatioAverage = filteredResults.stream()
//                    .mapToDouble(ResultDTO::getNegativeRatio)
//                    .average()
//                    .orElse(0.0);
//
//            double neutralRatioAverage = filteredResults.stream()
//                    .mapToDouble(ResultDTO::getNeutralRatio)
//                    .average()
//                    .orElse(0.0);
//
//            MonthlyUserStatisticsDTO monthlyStatistics = MonthlyUserStatisticsDTO.builder()
//                    .positiveRatio(positiveRatioAverage)
//                    .negativeRatio(negativeRatioAverage)
//                    .neutralRatio(neutralRatioAverage)
//                    .build();
//
//            monthlyStatisticsList.add(monthlyStatistics);
//        }
//
//        return monthlyStatisticsList;
//    }
    public static MonthlyUserStatisticsDTO calculateCurrentMonthStatistics(List<ResultDTO> resultDTOList) {
        // 현재 월을 기준으로 결과를 필터링합니다.
        YearMonth currentMonth = YearMonth.now();
        List<ResultDTO> currentMonthResults = resultDTOList.stream()
                .filter(resultDTO -> YearMonth.from(resultDTO.getDate()).equals(currentMonth))
                .collect(Collectors.toList());

        // 현재 월에 대한 평균을 계산합니다.
        double positiveRatioAverage = currentMonthResults.stream()
                .mapToDouble(ResultDTO::getPositiveRatio)
                .average()
                .orElse(0.0);

        double negativeRatioAverage = currentMonthResults.stream()
                .mapToDouble(ResultDTO::getNegativeRatio)
                .average()
                .orElse(0.0);

        double neutralRatioAverage = currentMonthResults.stream()
                .mapToDouble(ResultDTO::getNeutralRatio)
                .average()
                .orElse(0.0);

        // 현재 월에 대한 MonthlyUserStatisticsDTO를 생성하고 반환합니다.
        return MonthlyUserStatisticsDTO.builder()
                .positiveRatio(positiveRatioAverage)
                .negativeRatio(negativeRatioAverage)
                .neutralRatio(neutralRatioAverage)
                .build();
    }
}