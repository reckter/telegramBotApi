package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StickerRequest {

    @JsonProperty("chat_id")
    Long chatId;

    String sticker;

    @JsonProperty("disable_notification")
    Boolean disableNotifications;

    @JsonProperty("reply_to_message_id")
    Long replyTo;


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public Boolean getDisableNotifications() {
        return disableNotifications;
    }

    public void setDisableNotifications(Boolean disableNotifications) {
        this.disableNotifications = disableNotifications;
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }
}
