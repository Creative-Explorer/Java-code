package spring_security.JWT_Token.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring_security.JWT_Token.entity.MyUser;
import spring_security.JWT_Token.repository.MyUserRepository;

import java.util.Arrays;
import java.util.Collections;

@Service
public class MyUserDetailService implements UserDetailsService {


    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser userObj = myUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder().username(userObj.getUsername()).password(userObj.getPassword()).roles(getRoles(userObj)).build();
    }

    private String getRoles(MyUser user) {
        String roles = user.getRole();
        if (roles == null || roles.isEmpty()) {
            return Collections.singletonList("USER").toString();
        } else {
            return Arrays.asList(roles.split("\\s*,\\s*")).toString();
        }
    }

}
