package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.userdetails.User;

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
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @Column(name = "picture_diary", nullable = false)
    private String pictureDiary;

    @Column(name = "sentiment", length = 20, nullable = false)
    private String sentiment;

    @Column(name = "date", nullable = false)
    private Timestamp date;
}
