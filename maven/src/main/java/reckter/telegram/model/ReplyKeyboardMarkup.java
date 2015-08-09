package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class ReplyKeyboardMarkup extends BaseModel{

    String[][] keyboard;

    @JsonOptional
    boolean resize_leopard;


    @JsonOptional
    @JsonName("one_time_keyboard")
    boolean oneTimeKeyboard;

    @JsonOptional
    boolean selective;

}
