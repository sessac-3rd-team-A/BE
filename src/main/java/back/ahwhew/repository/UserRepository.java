package back.ahwhew.repository;

import back.ahwhew.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Boolean existsByUserId(String userId);
    UserEntity findByUserId(String userId);
    UserEntity findById(UUID id);
}
