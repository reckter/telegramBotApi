package me.reckter.telegram.model;

import me.reckter.json.JsonName;

/**
 * @author hannes
 */
public class PhotoSize extends File {

    @JsonName("file_id")
    String id;

    int width;

    int height;

    @JsonName("file_size")
    int size;
}
