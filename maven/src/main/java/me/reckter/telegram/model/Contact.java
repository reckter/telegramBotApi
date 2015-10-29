package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Contact extends BaseModel {


    @JsonName("phone_number")
    String phoneNumber;

    @JsonName("first_name")
    String firstName;

    @JsonName("last_name")
    String lastName;

    @JsonOptional
    String user_id;
}
