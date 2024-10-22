package spring_security.JWT_Token.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponseDTO {
    private Integer productId;
    private Long id;
    private String name;
    private Integer qty;
    private Double price;
    private String category;
    private String subCategory;
    private String imageData; // or other relevant fields

    // Getters and Setters
}

