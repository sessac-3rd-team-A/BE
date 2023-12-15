package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
//import org.springframework.security.core.userdetails.User;

import java.sql.Timestamp;

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

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity userId;

    @Column(name = "pictureDiary", nullable = false)
    private String pictureDiary;

    @Column(name = "sentiment", length = 20, nullable = false)
    private String sentiment;//대표감정

    //세부감정 Ratio
    @Column(name="positive",nullable=false)
    private double positive;

    @Column(name="negetive", nullable = false)
    private double negetive;

    @Column(name="neutral", nullable = false)
    private double neutral;

    @Column(name = "date", nullable = false)
    private Timestamp date;

    @Column(name="recommended_gif",nullable = false)
    private String recommendedGif;


}
