package spring_security.JWT_Token.service;

import lombok.Data;

@Data
public class VideoApiResponse {
    private VideoId id; // Change this to an object
    private Snippet snippet;

    @Data
    public static class VideoId {
        private String kind;      // "youtube#video"
        private String videoId;   // Actual video ID
    }

    @Data
    public static class Snippet {
        private String title;
        private String description;
    }
}
