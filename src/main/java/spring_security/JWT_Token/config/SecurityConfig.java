package spring_security.JWT_Token.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring_security.JWT_Token.Utils.JwtAuthFilter;
import spring_security.JWT_Token.Utils.UserInfoUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthFilter authFilter;

    @Bean
    //authentication
    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("Basant")
//                .password(encoder.encode("Pwd1"))
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withUsername("John")
//                .password(encoder.encode("Pwd2"))
//                .roles("USER","ADMIN","HR")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
        return new UserInfoUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable().authorizeHttpRequests().requestMatchers(
                "/products/new", "/products/authenticate",
                        "/products/save/add", "/Users/login", "/products/list", "/products/excel/upload",
                        "/Users/send-otp", "/Users/verify-otp",
                        "/router/sendOTP", "/router/validateOTP",
                        "/products/delete", "/Users/reset-password",
                        "/products/file-upload","/products/product-count",
                        "/products/image-upload","products/video/upload","products/video/list",
                        "/products/update-product", "/products/tojson","/Users/verifyUser",
                        "/Users/logout", "/Users/user-details",
                        "/carts/add-cart-items", "/carts/add-cart-items",
                        "/carts/cart-items", "/carts/item-count",
                        "/carts/cart-delete", "/Users/decrypt-password",
                        "/products/detailed", "/swagger-ui.html", "/v3/api-docs/**",
                        "/swagger-ui/**","/Users/encryptPassword","/payments/process",
                        "/payments/refund","/payments/history","/api/v1/openai/generate",
                        "/api/v1/voice/convert","/api/v1/news/top-headlines",
                        "/api/v1/news/all","/api/v1/news/search","/youtube/videos/category",
                        "/youtube/videos/trending","/youtube/videos/all"
                        )
                .permitAll().and().authorizeHttpRequests()
                .requestMatchers("/products/**","/carts/**","/registration/**",
                        "/Users/**","/payments/**","/youtube")
                .authenticated().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}