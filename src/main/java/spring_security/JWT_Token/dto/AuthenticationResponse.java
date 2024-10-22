package spring_security.JWT_Token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private LocalDateTime lastLoginTime;
    private String userName;
    private String phoneNumber;
    private String role;
}
