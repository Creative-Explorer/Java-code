package spring_security.JWT_Token.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for auto-increment integer IDs
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Foreign key column in CartEntity table
    @JsonIgnore
    private UserInfoEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference // Prevent infinite recursion
    private ProductEntity product;

    @CreationTimestamp
    @Column(updatable = false) // Optional: Make sure `createdAt` is not updated
    private Date createdAt;

    private int qty;

    private double price;

    private String name;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
}
