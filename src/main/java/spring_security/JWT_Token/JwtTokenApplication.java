package spring_security.JWT_Token;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableAsync
public class JwtTokenApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		String encodedString = "RNALvoclHnUlt/Yzn3avt6kJszU=";

		// Decode the Base64 string
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

		// Try different encodings (UTF-8, ISO-8859-1, etc.)
		String decodedStringUtf8 = new String(decodedBytes, Charset.forName("UTF-8"));
		String decodedStringIso = new String(decodedBytes, Charset.forName("ISO-8859-1"));

		// Print results
		System.out.println("UTF-8 Decoded: " + decodedStringUtf8);
		System.out.println("ISO-8859-1 Decoded: " + decodedStringIso);
	}

}
