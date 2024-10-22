package spring_security.JWT_Token;

import lombok.Getter;

@Getter
public enum EnumConstants {
    PENDING("Pending"),
    SUCCESS("Success"),
    REFUNDED("Refunded"),
    PROCESSING("Processing");

    private final String displayName;

    EnumConstants(String displayName) {
        this.displayName = displayName;
    }
}
