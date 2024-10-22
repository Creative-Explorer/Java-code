package spring_security.JWT_Token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring_security.JWT_Token.entity.VideoData;

@Repository
public interface VideoRepository extends JpaRepository<VideoData, Long> {
}
