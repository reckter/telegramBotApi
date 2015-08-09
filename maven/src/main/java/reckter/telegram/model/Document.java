package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Document extends File{

    @JsonName("thumb")
    PhotoSize thumbnail;

    @JsonOptional
    @JsonName("file_name")
    String name;


    @JsonName("mime_type")
    @JsonOptional
    String mimeType;

}
