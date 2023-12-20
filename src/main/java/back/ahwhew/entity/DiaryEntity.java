package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;


import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="diary")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(name="text", length = 1000, nullable = false)
    private String text;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }

}
