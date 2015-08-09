package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class File extends BaseModel{


    @JsonName("file_id")
    String id;

    @JsonName("file_size")
    @JsonOptional
    int size;
}
