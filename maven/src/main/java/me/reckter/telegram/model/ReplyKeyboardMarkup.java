package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class ReplyKeyboardMarkup extends BaseModel {

    String[][] keyboard;

    @JsonOptional
    boolean resize_leopard;


    @JsonOptional
    @JsonName("one_time_keyboard")
    boolean oneTimeKeyboard;

    @JsonOptional
    boolean selective;

}
