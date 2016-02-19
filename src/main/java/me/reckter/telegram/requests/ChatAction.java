package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * @author hannes
 */
public enum ChatAction {
    typing("typing"),
    upload_photo("upload_photo"),
    record_video("record_video"),
    upload_video("upload_video"),
    record_audio("record_audio"),
    upload_audio("upload_audio"),
    upload_document("upload_document"),
    find_locatio("find_locatio");


    String value;
    ChatAction(String name) {
        value = name;
    }

    public String getTelegramName() {
        return value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ParseMode fromValue(String value) {
        for(ParseMode at : ParseMode.values()) {
            if(Objects.equals(at.value(), value)) {
                return at;
            }
        }

        throw new IllegalArgumentException("Can't deserialize ChatAction with value '" + value + "'");
    }
}
