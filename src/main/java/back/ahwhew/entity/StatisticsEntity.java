package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "statistics")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="positive", nullable = false)
    private double positive;

    @Column(name="negative", nullable = false)
    private double negative;

    @Column(name="neutral", nullable = false)
    private double neutral;

    @Column(name="date", nullable = false)
    private LocalDate date;

    @Column(name="recommended_gif",nullable = false)
    private String recommendedGif;

    @Column(name="gender",length = 1)
    private Character gender;

    @Column(name="age")
    private String age;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }
}
