package back.ahwhew.repository;

import back.ahwhew.entity.DiaryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;
@Transactional
public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DiaryEntity d WHERE d.userId = :userId AND d.date = :date")
    boolean existsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE DiaryEntity SET text = :text WHERE userId = :userId AND date = :date")
    void update(@Param("userId") UUID userId, @Param("text") String text, @Param("date") LocalDate date);

    default void save(UUID userId, String text) {
        LocalDate today = LocalDate.now();
        boolean diaryExists = existsByUserIdAndDate(userId, today);

        if (!diaryExists) {
            DiaryEntity diaryEntity = new DiaryEntity();
            diaryEntity.setUserId(userId);
            diaryEntity.setText(text);
            diaryEntity.setDate(today);
            save(diaryEntity);
        } else {
            update(userId, text, today);
        }
    }
}
