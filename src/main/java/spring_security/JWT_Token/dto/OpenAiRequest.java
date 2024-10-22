package spring_security.JWT_Token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiRequest {
    private String model;  // The model to use, e.g., "gpt-3.5-turbo"
    private List<Message> messages;  // Messages for the chat-style request
    private double temperature;      // Controls randomness in output (0-1)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;         // Role of the message: "system", "user", or "assistant"
        private String content;      // The actual message content
    }
}
