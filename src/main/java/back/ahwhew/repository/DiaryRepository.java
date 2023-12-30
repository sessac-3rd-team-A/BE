package back.ahwhew.repository;

import back.ahwhew.entity.DiaryEntity;
import back.ahwhew.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DiaryEntity d WHERE d.user.id = :userId AND d.date = :date")
    boolean existsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE DiaryEntity SET text = :text, jobRelatedWords = :jobRelatedWords, jobCategories = :jobCategories WHERE user.id = :userId AND date = :date")
    void update(@Param("userId") UUID userId, @Param("text") String text, @Param("jobRelatedWords") String jobRelatedWords, @Param("jobCategories") String jobCategories, @Param("date") LocalDate date);

    @Transactional
    default void save(UserEntity user, String text, List<String> jobRelatedWords, List<String> jobCategories) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        boolean diaryExists = existsByUserIdAndDate(user.getId(), today);

        if (!diaryExists) {
            DiaryEntity diaryEntity = new DiaryEntity();
            diaryEntity.setUser(user);
            diaryEntity.setText(text);
            diaryEntity.setJobRelatedWords(String.join(",", jobRelatedWords));
            diaryEntity.setJobCategories(String.join(",", jobCategories));
            diaryEntity.setDate(today);
            save(diaryEntity);
        } else {
            update(user.getId(), text, String.join(",", jobRelatedWords), String.join(",", jobCategories), today);
        }
    }

    DiaryEntity findTopByUserIdOrderByDateDesc(UUID id);
}