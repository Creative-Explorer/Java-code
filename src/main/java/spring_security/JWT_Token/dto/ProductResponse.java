package spring_security.JWT_Token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring_security.JWT_Token.entity.ProductEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private ProductEntity product;
    private boolean isDisabled;
    private String paymentStatus;
}
