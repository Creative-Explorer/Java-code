package spring_security.JWT_Token.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import spring_security.JWT_Token.entity.UserInfoEntity;


public class SecurityUtils {
    public static UserInfoEntity getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        return (UserInfoEntity) principal;

    }

}