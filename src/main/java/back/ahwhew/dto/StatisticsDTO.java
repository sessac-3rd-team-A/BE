package back.ahwhew.dto;

import back.ahwhew.entity.StatisticsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data //getter + setter
public class StatisticsDTO {
    private Long id;
    private double positive;
    private double negative;
    private double neutral;
    private String recommentdedGif;
    private char gender;
    private String age;

    public StatisticsDTO(final StatisticsDTO entity){

        this.positive = entity.getPositive();
        this.negative = entity.getNegative();
        this.neutral = entity.getNeutral();
        this.gender=entity.getGender();
        this.age=entity.getAge();
        // date,id 숨김처리
    }
    // DTO를 entity로 반환하는 메소드
    public static StatisticsEntity toEntity(final StatisticsDTO dto){
        return StatisticsEntity.builder()
                .positive(dto.getPositive())
                .negative(dto.getNegative())
                .neutral(dto.getNeutral())
                .gender(dto.getGender())
                .age(dto.getAge())
                .build();
    }
}
