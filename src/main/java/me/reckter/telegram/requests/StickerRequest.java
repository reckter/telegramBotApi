package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StickerRequest {

    @JsonProperty("chat_id")
    public long chat;

    public File sticker;

    @JsonProperty("disable_notification")
    public boolean disableNotification;

    @JsonProperty("reply_to_message_id")
    public Integer replyTo;

}
