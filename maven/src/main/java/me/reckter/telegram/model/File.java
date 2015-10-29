package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class File extends BaseModel {


    @JsonName("file_id")
    String id;

    @JsonName("file_size")
    @JsonOptional
    int size;
}
