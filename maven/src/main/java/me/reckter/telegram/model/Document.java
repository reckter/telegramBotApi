package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Document extends File {

    @JsonName("thumb")
    PhotoSize thumbnail;

    @JsonOptional
    @JsonName("file_name")
    String name;


    @JsonName("mime_type")
    @JsonOptional
    String mimeType;

}
