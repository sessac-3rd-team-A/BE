package back.ahwhew.entity;

import back.ahwhew.dto.ResultDTO;
import back.ahwhew.service.UserService;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "result")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = true)
    private UserEntity user;

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
    private LocalDate date;

    @Column(name="recommendedGif",nullable = false)
    private String recommendedGif;


    @PrePersist
    protected void onCreate() {
        date = LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

    @PreUpdate
    protected void onUpdate()  {
        date = LocalDate.now(ZoneId.of("Asia/Seoul"));
    }
}
