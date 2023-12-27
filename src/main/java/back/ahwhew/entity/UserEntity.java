package back.ahwhew.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "age", nullable = false)
    private String age;

    @Column(name = "gender", nullable = false, length = 1)
    private char gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ResultEntity> results = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiaryEntity> diaries = new HashSet<>();


    public void addResult(ResultEntity result) {
        results.add(result);
        result.setUser(this);
    }

    public void removeResult(ResultEntity result) {
        results.remove(result);
        result.setUser(null);
    }

    public void addDiary(DiaryEntity diary) {
        diaries.add(diary);
        diary.setUser(this);
    }

    public void removeDiary(DiaryEntity diary) {
        diaries.remove(diary);
        diary.setUser(null);
    }
}
