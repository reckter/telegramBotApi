package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hannes
 */
public class Document extends File {

    @JsonProperty("thumb")
    PhotoSize thumbnail;


    @JsonProperty("file_name")
    String name;


    @JsonProperty("mime_type")

    String mimeType;


    public PhotoSize getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(PhotoSize thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
