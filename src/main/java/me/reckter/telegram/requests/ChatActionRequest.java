package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hannes on 18.02.16.
 */
public class ChatActionRequest {

    ChatAction action;

    @JsonProperty("chat_id")
    long id;


    public ChatAction getAction() {
        return action;
    }

    public void setAction(ChatAction action) {
        this.action = action;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
