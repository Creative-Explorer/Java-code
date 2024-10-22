package spring_security.JWT_Token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring_security.JWT_Token.dto.OpenAiRequest;
import spring_security.JWT_Token.dto.OpenAiResponse;
import spring_security.JWT_Token.service.OpenAiService;

@RestController
@RequestMapping("/api/v1/openai")
public class OpenAiController {

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/generate")
    public OpenAiResponse generateText(@RequestBody OpenAiRequest request) {
        return openAiService.generateText(request);
    }
}
