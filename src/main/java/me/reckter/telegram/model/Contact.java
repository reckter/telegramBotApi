package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hannes
 */
public class Contact extends BaseModel {


    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;


    String user_id;

    public Contact(String phoneNumber, String firstName, String lastName, String user_id) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user_id = user_id;
    }
}
