package spring_security.JWT_Token.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartDTO {

    private int id;
    private int productId;
    private String name;
    private int qty;
    private byte[] imageData;
    private double price;
    private Date createdAt;

}
