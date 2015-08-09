package reckter.telegram.model;

import reckter.json.JsonName;
import reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class User extends Chat{

    @JsonOptional
    @JsonName("first_name")
    String fistName;

    @JsonOptional
    @JsonName("last_name")
    String lastName;

    String username;
}
