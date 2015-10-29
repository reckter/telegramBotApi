package me.reckter.telegram.model;

import me.reckter.json.JsonName;

/**
 * @author hannes
 */
public class Error extends BaseModel{

    public String description;

    @JsonName("error_code")
    public int errorCode;

    public boolean ok;

}
