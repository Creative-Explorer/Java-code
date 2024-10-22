package spring_security.JWT_Token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Name {
    @JsonProperty("common")
    private String common;

    @JsonProperty("official")
    private String official;
}
