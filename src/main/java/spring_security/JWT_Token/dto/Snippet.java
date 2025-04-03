package spring_security.JWT_Token.dto;

import lombok.Data;

@Data
class Snippet { // Make sure this class is public if you want to access it outside its package
    private String title;
    private String description;
}
