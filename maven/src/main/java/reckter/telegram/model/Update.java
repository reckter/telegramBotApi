package reckter.telegram.model;

import reckter.json.JsonName;

/**
 * @author hannes
 */
public class Update extends BaseModel{

    @JsonName("update_id")
    public int id;

    public Message message;
}
