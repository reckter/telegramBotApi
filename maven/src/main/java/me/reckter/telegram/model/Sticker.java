package me.reckter.telegram.model;

import me.reckter.json.JsonName;

/**
 * @author hannes
 */
public class Sticker extends File {

    int width;

    int height;

    @JsonName("thumb")
    PhotoSize thumbnail;
}
