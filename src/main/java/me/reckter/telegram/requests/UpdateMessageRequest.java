package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hannes on 18/06/2016.
 */
public class UpdateMessageRequest extends MessageRequest {


    @JsonProperty("message_id")
    public long messageId;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
