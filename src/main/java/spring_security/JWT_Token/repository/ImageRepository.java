package spring_security.JWT_Token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring_security.JWT_Token.entity.ImageData;

@Repository
public interface ImageRepository extends JpaRepository<ImageData, Long> {
}
