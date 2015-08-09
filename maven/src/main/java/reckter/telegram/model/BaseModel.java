package reckter.telegram.model;

import reckter.json.JsonIgnore;
import reckter.telegram.Telegram;

/**
 * @author hannes
 */
public abstract class BaseModel {

    @JsonIgnore
    protected Telegram telegram;
}
