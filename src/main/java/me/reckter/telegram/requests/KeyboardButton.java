package me.reckter.telegram.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyboardButton {

    public KeyboardButton() {
    }

    public KeyboardButton(String text, Boolean requestContact, Boolean requestLocation) {
        this.text = text;
        this.requestContact = requestContact;
        this.requestLocation = requestLocation;
    }

    String text;

    @JsonProperty("request_contact")
    Boolean	requestContact;

    @JsonProperty("request_location")
    Boolean requestLocation;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getRequestContact() {
        return requestContact;
    }

    public void setRequestContact(Boolean requestContact) {
        this.requestContact = requestContact;
    }

    public Boolean getRequestLocation() {
        return requestLocation;
    }

    public void setRequestLocation(Boolean requestLocation) {
        this.requestLocation = requestLocation;
    }
}
