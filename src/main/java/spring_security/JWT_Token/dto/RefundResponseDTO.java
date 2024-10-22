package spring_security.JWT_Token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponseDTO {

    private Long paymentId;
    private double amount;
    private String status;
}
