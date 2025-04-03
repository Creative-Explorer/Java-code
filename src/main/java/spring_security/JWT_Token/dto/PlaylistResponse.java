package spring_security.JWT_Token.dto;

import java.util.List;

public class PlaylistResponse {
    private List<Playlist> items;

    public List<Playlist> getItems() {
        return items;
    }

    public void setItems(List<Playlist> items) {
        this.items = items;
    }
}
