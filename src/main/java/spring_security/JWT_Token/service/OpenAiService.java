package spring_security.JWT_Token.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spring_security.JWT_Token.dto.OpenAiRequest;
import spring_security.JWT_Token.dto.OpenAiResponse;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public OpenAiResponse generateText(OpenAiRequest request) {
        String endpoint = apiUrl + "/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);
        OpenAiResponse responseBody = null;

        try {
            // Send a single request
            ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );
            responseBody = response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            // Additional error handling
        }

        return responseBody; // Return the API response or null if there's an error
    }
}
