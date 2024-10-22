package spring_security.JWT_Token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_security.JWT_Token.entity.CartEntity;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartEntity, Integer> {
    List<CartEntity> findByUserId(Integer userId);
}
