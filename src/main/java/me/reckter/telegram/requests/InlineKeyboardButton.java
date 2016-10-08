package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InlineKeyboardButton {

    public InlineKeyboardButton() {
    }

    public InlineKeyboardButton(String text, String url, String callbackData, String switchInlineQuery) {
        this.text = text;
        this.url = url;
        this.callbackData = callbackData;
        this.switchInlineQuery = switchInlineQuery;
    }

    String text;

    String url;

    @JsonProperty("callback_data")
    String	callbackData;

    @JsonProperty("switch_inline_query")
    String switchInlineQuery;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }

    public String getSwitchInlineQuery() {
        return switchInlineQuery;
    }

    public void setSwitchInlineQuery(String switchInlineQuery) {
        this.switchInlineQuery = switchInlineQuery;
    }
}
