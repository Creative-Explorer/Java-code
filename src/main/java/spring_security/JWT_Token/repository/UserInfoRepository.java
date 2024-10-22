package spring_security.JWT_Token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring_security.JWT_Token.entity.UserInfoEntity;
import spring_security.JWT_Token.entity.UserInfoEntity;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Integer> {
    Optional<UserInfoEntity> findByName(String username);

    Optional<UserInfoEntity> findByPhoneNumber(String phoneNumber);
}
