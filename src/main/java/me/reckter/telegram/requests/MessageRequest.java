package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hannes on 13.02.16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRequest {

    @JsonProperty("chat_id")
    String id;

    String text;

    @JsonProperty("disable_notification")
    public boolean disableNotification;

    @JsonProperty("parse_mode")
    ParseMode parseMode;

    @JsonProperty("disable_web_page_preview")
    boolean disableWebPagePreview;

    @JsonProperty("reply_to_message_id")
    int replyTo;

    @JsonProperty("reply_markup")
    ReplyMarkup replyMarkup;


    public MessageRequest(MessageRequest messageRequest) {
        this.id = messageRequest.getId();
        this.text = messageRequest.getText();
        this.parseMode = messageRequest.getParseMode();
        this.disableWebPagePreview = messageRequest.disableWebPagePreview;
        this.replyTo = messageRequest.getReplyTo();
    }

    public MessageRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ParseMode parseMode) {
        this.parseMode = parseMode;
    }

    public boolean isDisableWebPagePreview() {
        return disableWebPagePreview;
    }

    public void setDisableWebPagePreview(boolean disableWebPagePreview) {
        this.disableWebPagePreview = disableWebPagePreview;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }

    public boolean isDisableNotification() {
        return disableNotification;
    }

    public void setDisableNotification(boolean disableNotification) {
        this.disableNotification = disableNotification;
    }

    public ReplyMarkup getReplyMarkup() {
        return replyMarkup;
    }

    public void setReplyMarkup(ReplyMarkup replyMarkup) {
        this.replyMarkup = replyMarkup;
    }
}
