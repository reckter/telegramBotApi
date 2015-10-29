package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Audio extends File {


    int duration;

    @JsonName("mime_type")
    @JsonOptional
    String mimeType;

}
