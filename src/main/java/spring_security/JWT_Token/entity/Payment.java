package spring_security.JWT_Token.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring_security.JWT_Token.EnumConstants;
import spring_security.JWT_Token.entity.UserInfoEntity;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private EnumConstants status;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserInfoEntity user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    @JsonIgnore
//    private List<ProductEntity> product; // Ensure that this is correctly mapped

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "payment_product",
            joinColumns = @JoinColumn(name = "payment_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JsonIgnore
    private List<ProductEntity> products;


    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Lob
    @Column(name = "payment_receipt", length = 1000)
    private byte[] paymentReceipt;

    @Column(name = "refund_amount", nullable = false, columnDefinition = "Decimal(10,2) default 0.00")
    private Double refundAmount = 0.0;


    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "refund_date")
    private Date refundDate;
}
