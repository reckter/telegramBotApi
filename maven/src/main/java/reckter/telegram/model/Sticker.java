package reckter.telegram.model;

import reckter.json.JsonName;

/**
 * @author hannes
 */
public class Sticker extends File{

    int width;

    int height;

    @JsonName("thumb")
    PhotoSize thumbnail;
}
