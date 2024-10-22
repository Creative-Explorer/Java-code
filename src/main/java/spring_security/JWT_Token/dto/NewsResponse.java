package spring_security.JWT_Token.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;

    @Data
    public static class Article {
        private String title;
        private String description;
        private String url;
        private String urlToImage;
    }
}
