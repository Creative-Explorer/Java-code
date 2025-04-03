package spring_security.JWT_Token.dto;

import spring_security.JWT_Token.dto.ChannelDetails;

import java.util.List;

public class ChannelResponse {
    private List<ChannelDetails> items;

    public List<ChannelDetails> getItems() {
        return items;
    }

    public void setItems(List<ChannelDetails> items) {
        this.items = items;
    }
}
