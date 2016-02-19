package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hannes
 */
public class ReplyKeyboardMarkup extends BaseModel {

    String[][] keyboard;


    boolean resize_leopard;



    @JsonProperty("one_time_keyboard")
    boolean oneTimeKeyboard;


    boolean selective;


    public String[][] getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(String[][] keyboard) {
        this.keyboard = keyboard;
    }

    public boolean isResize_leopard() {
        return resize_leopard;
    }

    public void setResize_leopard(boolean resize_leopard) {
        this.resize_leopard = resize_leopard;
    }

    public boolean isOneTimeKeyboard() {
        return oneTimeKeyboard;
    }

    public void setOneTimeKeyboard(boolean oneTimeKeyboard) {
        this.oneTimeKeyboard = oneTimeKeyboard;
    }

    public boolean isSelective() {
        return selective;
    }

    public void setSelective(boolean selective) {
        this.selective = selective;
    }
}
