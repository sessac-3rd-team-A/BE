package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
//import org.springframework.security.core.userdetails.User;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "result")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId",nullable=true)
    private UUID userId;

    @Column(name = "pictureDiary", nullable = false)
    private String pictureDiary;

    @Column(name = "sentiment", length = 20, nullable = false)
    private String sentiment;//대표감정

    //세부감정 Ratio
    @Column(name="positiveRatio",nullable=false)
    private double positiveRatio;

    @Column(name="negativeRatio", nullable = false)
    private double negativeRatio;

    @Column(name="neutralRatio", nullable = false)
    private double neutralRatio;

    @Column(name = "date", nullable = false)
    private Timestamp date;

    @Column(name="recommendedGif",nullable = false)
    private String recommendedGif;


}
