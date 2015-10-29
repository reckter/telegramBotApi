package me.reckter.telegram.model;

import me.reckter.json.JsonName;

/**
 * @author hannes
 */
public class Update extends BaseModel {

    @JsonName("update_id")
    public int id;

    public Message message;
}
