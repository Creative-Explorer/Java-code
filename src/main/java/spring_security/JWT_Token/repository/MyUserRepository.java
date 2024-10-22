package spring_security.JWT_Token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_security.JWT_Token.entity.MyUser;

import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {


    Optional<MyUser> findByUsername(String username);
}
