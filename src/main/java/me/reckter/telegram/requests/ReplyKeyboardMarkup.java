package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplyKeyboardMarkup extends ReplyMarkup {

    List<List<KeyboardButton>> keyboard;


    @JsonProperty("resize_keyboard")
    Boolean	resizeKeyboard;

    @JsonProperty("one_time_keyboard")
    Boolean	oneTimeKeyboard;

    Boolean selective;

    public List<List<KeyboardButton>> getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(List<List<KeyboardButton>> keyboard) {
        this.keyboard = keyboard;
    }

    public Boolean getResizeKeyboard() {
        return resizeKeyboard;
    }

    public void setResizeKeyboard(Boolean resizeKeyboard) {
        this.resizeKeyboard = resizeKeyboard;
    }

    public Boolean getOneTimeKeyboard() {
        return oneTimeKeyboard;
    }

    public void setOneTimeKeyboard(Boolean oneTimeKeyboard) {
        this.oneTimeKeyboard = oneTimeKeyboard;
    }

    public Boolean getSelective() {
        return selective;
    }

    public void setSelective(Boolean selective) {
        this.selective = selective;
    }
}
