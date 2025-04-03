package spring_security.JWT_Token.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videoData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String path;

    @Lob
    @Column(name = "video_data")
    private byte[] videoData;

    private String type;

//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Customize format as needed
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Customize format as needed
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    public void onPrePersist() {
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    public void onPreUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }

}
