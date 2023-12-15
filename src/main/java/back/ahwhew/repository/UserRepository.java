package back.ahwhew.repository;

import back.ahwhew.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Boolean existsByUserId(String userId);

//    UserEntity findByUserId(String userId);
}
