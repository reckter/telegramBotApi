package me.reckter.telegram.model;

import me.reckter.json.JsonIgnore;
import me.reckter.telegram.Telegram;

/**
 * @author hannes
 */
public abstract class BaseModel {

    @JsonIgnore
    public Telegram telegram;
}
