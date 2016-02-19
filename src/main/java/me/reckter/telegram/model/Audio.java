package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.reckter.telegram.model.File;

/**
 * @author hannes
 */
public class Audio extends File {


    int duration;

    @JsonProperty("mime_type")
    String mimeType;


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
