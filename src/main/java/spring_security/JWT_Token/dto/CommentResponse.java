package spring_security.JWT_Token.dto;

import javax.xml.stream.events.Comment;
import java.util.List;

public class CommentResponse {
    private List<Comment> items;

    public List<Comment> getItems() {
        return items;
    }

    public void setItems(List<Comment> items) {
        this.items = items;
    }
}
