package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class Contact extends BaseModel{


    @JsonName("phone_number")
    String phoneNumber;

    @JsonName("first_name")
    String firstName;

    @JsonName("last_name")
    String lastName;

    @JsonOptional
    String user_id;
}
