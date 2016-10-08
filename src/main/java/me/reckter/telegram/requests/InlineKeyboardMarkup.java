package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannes Güdelhöfer
 */
public class InlineKeyboardMarkup extends ReplyMarkup {

    @JsonProperty("inline_keyboard")
    List<List<InlineKeyboardButton>> inlineKeyboard;

    public List<List<InlineKeyboardButton>> getInlineKeyboard() {
        return inlineKeyboard;
    }

    public void setInlineKeyboard(List<List<InlineKeyboardButton>> inlineKeyboard) {
        this.inlineKeyboard = inlineKeyboard;
    }
}
