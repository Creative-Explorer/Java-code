package spring_security.JWT_Token.dto;

import spring_security.JWT_Token.dto.VideoDetails;

import java.util.List;

public class VideoDetailsResponse {
    private List<VideoDetails> items;

    public List<VideoDetails> getItems() {
        return items;
    }

    public void setItems(List<VideoDetails> items) {
        this.items = items;
    }
}
