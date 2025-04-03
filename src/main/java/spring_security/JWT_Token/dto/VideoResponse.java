package spring_security.JWT_Token.dto;

import lombok.Data;
import spring_security.JWT_Token.service.VideoApiResponse;

import java.util.List;

@Data
public class VideoResponse {
    private String kind; // YouTube response kind
    private String etag; // ETag value
    private List<VideoApiResponse> items; // List of videos
}
