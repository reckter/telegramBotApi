package me.reckter.telegram.model.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.reckter.telegram.model.BaseModel;
import me.reckter.telegram.model.Message;

/**
 * @author hannes
 */
public class Update extends BaseModel {

    @JsonProperty("update_id")
    public int id;

    public Message message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


}
