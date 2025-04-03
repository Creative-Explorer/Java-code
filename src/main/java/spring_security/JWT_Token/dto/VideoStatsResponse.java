package spring_security.JWT_Token.dto;

import spring_security.JWT_Token.dto.VideoStats;

import java.util.List;

public class VideoStatsResponse {
    private List<VideoStats> items;

    public List<VideoStats> getItems() {
        return items;
    }

    public void setItems(List<VideoStats> items) {
        this.items = items;
    }
}
