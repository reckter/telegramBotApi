package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hannes on 18.02.16.
 */
public class LocationRequest {

    @JsonProperty("chat_id")
    long id;


    float latitude;

    float longitude;

    @JsonProperty("reply_to_message_id")
    int replyTo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }
}
