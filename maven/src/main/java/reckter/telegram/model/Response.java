package reckter.telegram.model;

import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Response extends BaseModel{

    @JsonOptional
    public boolean ok;

    public Object[] result;
}
