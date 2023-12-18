package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "gif")
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GifEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "gifUrl", nullable = false)
    private String gifUrl;

    @Column (name="tag" , nullable = false)
    private String tag;
}
