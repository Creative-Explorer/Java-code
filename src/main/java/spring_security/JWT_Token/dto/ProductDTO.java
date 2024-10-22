package spring_security.JWT_Token.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {
//    private int productId;
    private String name;
    private Integer qty;
    private Double price;
    private String phoneNumber;
    private String category;
    private String subCategory;
    private MultipartFile imageData;
    private boolean soldOut;

    // Getters and Setters
}
