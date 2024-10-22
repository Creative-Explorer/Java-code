package spring_security.JWT_Token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring_security.JWT_Token.entity.ProductEntity;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {


    List<ProductEntity> findByUserId(Long id);

    long countByUserId(Long id);
}
