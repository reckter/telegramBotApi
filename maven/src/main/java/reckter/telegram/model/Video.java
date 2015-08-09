package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

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
