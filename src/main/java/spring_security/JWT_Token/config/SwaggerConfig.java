package spring_security.JWT_Token.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi bookApi() {
        return GroupedOpenApi.builder()
                .group("spring_security.JWT_Token.config")
                .pathsToMatch("/products/**","/carts/**",
                        "/registration/**", "/Users/**","/payments/**").build();
    }
}
