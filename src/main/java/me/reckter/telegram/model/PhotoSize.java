package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hannes
 */
public class PhotoSize extends File {

    @JsonProperty("file_id")
    String id;

    int width;

    int height;

    @JsonProperty("file_size")
    int size;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }
}
