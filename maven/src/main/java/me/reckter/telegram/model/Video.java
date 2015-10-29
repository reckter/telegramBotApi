package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Video extends File {

    int width;

    int height;

    int duration;

    @JsonName("thumb")
    PhotoSize thumbnail;


    @JsonName("mime_type")
    @JsonOptional
    String mimeType;

    @JsonOptional
    String caption;
}
