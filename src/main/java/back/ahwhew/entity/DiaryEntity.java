package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(name="text", length = 1000, nullable = false)
    private String text;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
