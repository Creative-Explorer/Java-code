package spring_security.JWT_Token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring_security.JWT_Token.entity.ProductEntity;

import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedProductsResponse {

    private Map<String, List<ProductEntity>> electronics;
    private Map<String, List<ProductEntity>> clothes;

    // Constructors, Getters, and Setters
}
