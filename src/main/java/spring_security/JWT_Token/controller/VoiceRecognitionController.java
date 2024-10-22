package spring_security.JWT_Token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring_security.JWT_Token.dto.VoiceRequest;
import spring_security.JWT_Token.dto.VoiceResponse;
import spring_security.JWT_Token.service.VoiceRecognitionService;


@RestController
@RequestMapping("/api/v1/voice")
public class VoiceRecognitionController {

    @Autowired
    private VoiceRecognitionService voiceRecognitionService;

    @PostMapping("/convert")
    public VoiceResponse convertVoiceToText(@RequestBody VoiceRequest request) {
        return voiceRecognitionService.convertVoiceToText(request);
    }
}
