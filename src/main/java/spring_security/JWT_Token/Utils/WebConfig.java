//package spring_security.JWT_Token.Utils;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.http.converter.HttpMessageConverter;
//
//import java.util.List;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    private final YamlHttpMessageConverter yamlHttpMessageConverter;
//
//    public WebConfig(YamlHttpMessageConverter yamlHttpMessageConverter) {
//        this.yamlHttpMessageConverter = yamlHttpMessageConverter;
//    }
//
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(yamlHttpMessageConverter);
//    }
//}
