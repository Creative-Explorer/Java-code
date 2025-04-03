package spring_security.JWT_Token.dto;

import spring_security.JWT_Token.dto.Video;

import java.util.List;

public class Playlist {
    private String id;
    private String title;
    private String description;
    private List<Video> videos; // Assuming you have a Video class

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
