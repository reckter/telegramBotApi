package me.reckter.telegram.model;

import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Response extends BaseModel {

    @JsonOptional
    public boolean ok;

    public Object[] result;
}
