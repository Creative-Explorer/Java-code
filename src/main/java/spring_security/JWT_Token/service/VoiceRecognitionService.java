package spring_security.JWT_Token.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import spring_security.JWT_Token.dto.VoiceRequest;
import spring_security.JWT_Token.dto.VoiceResponse;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

@Service
public class VoiceRecognitionService {

    public VoiceResponse convertVoiceToText(VoiceRequest request) {
        VoiceResponse response = new VoiceResponse();
        try {
            byte[] audioBytes = Base64.getDecoder().decode(request.getAudioBase64());
            InputStream audioInputStream = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioInputStream);
            String transcribedText = performVoiceRecognition(audioStream);
            response.setTranscribedText(transcribedText);
            response.setMessage("Success");
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    private String performVoiceRecognition(AudioInputStream audioStream) {
        try {
            // Convert AudioInputStream to ByteString
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            ByteString audioBytes = ByteString.copyFrom(byteArrayOutputStream.toByteArray());

            // Set up the Speech client
            try (SpeechClient speechClient = SpeechClient.create()) {
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("en-US")
                        .build();
                RecognizeRequest request = RecognizeRequest.newBuilder()
                        .setConfig(config)
                        .setAudio(RecognitionAudio.newBuilder().setContent(audioBytes).build())
                        .build();

                RecognizeResponse recognizeResponse = speechClient.recognize(request);
                StringBuilder transcript = new StringBuilder();
                for (SpeechRecognitionResult result : recognizeResponse.getResultsList()) {
                    transcript.append(result.getAlternatives(0).getTranscript());
                }
                return transcript.toString();
            }
        } catch (Exception e) {
            return "Error during recognition: " + e.getMessage();
        }
    }
}
