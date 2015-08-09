package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Audio extends File{


    int duration;

    @JsonName("mime_type")
    @JsonOptional
    String mimeType;

}
