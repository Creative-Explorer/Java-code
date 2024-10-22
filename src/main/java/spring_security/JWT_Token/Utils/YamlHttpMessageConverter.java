package spring_security.JWT_Token.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class YamlHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public YamlHttpMessageConverter() {
        super(new ObjectMapper(new YAMLFactory()),
                MediaType.parseMediaType("application/x-yaml"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object == null) {
            throw new HttpMessageNotWritableException("No content to write");
        }
        super.writeInternal(object, outputMessage);
    }
}
