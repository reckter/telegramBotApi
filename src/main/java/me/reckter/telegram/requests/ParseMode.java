package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * Created by hannes on 13.02.16.
 */
public enum ParseMode {
    MARKDOWN_V2("MarkdownV2"),
    MARKDOWN("Markdown"),
    HTML("HTML"),
    NONE("");


    String telegramName;
    ParseMode(String name) {
        telegramName = name;
    }

    public String getTelegramName() {
        return telegramName;
    }

    @JsonValue
    public String value() {
        return this.telegramName;
    }

    @JsonCreator
    public static ParseMode fromValue(String value) {
        for(ParseMode at : ParseMode.values()) {
            if(Objects.equals(at.value(), value)) {
                return at;
            }
        }

        throw new IllegalArgumentException("Can't deserialize ParseMode with value '" + value + "'");
    }
}
