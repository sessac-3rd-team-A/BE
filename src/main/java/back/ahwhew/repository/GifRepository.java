package back.ahwhew.repository;

import back.ahwhew.entity.GifEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GifRepository extends JpaRepository<GifEntity, Long> {
     GifEntity findByGifUrl(String recommendedGif);

    List<GifEntity> findByTag(String tag);

    List<GifEntity> findAllByTag(String classifyTag);
}
