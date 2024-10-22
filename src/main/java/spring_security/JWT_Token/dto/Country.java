package spring_security.JWT_Token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring_security.JWT_Token.dto.Name;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Country {
    @JsonProperty("name")
    private Name name;

    @JsonProperty("latlng")
    private double[] latlng;

    public double getLatitude() {
        return latlng != null && latlng.length > 0 ? latlng[0] : 0;
    }

    public double getLongitude() {
        return latlng != null && latlng.length > 1 ? latlng[1] : 0;
    }
}

