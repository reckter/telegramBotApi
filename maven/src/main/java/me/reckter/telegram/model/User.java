package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

/**
 * @author hannes
 */
public class User extends Chat {

    @JsonName("first_name")
    public String fistName;

    @JsonOptional
    @JsonName("last_name")
    public String lastName;

    @JsonOptional
    public String username;

    @JsonOptional
    public String type;

    @Override
    public String toString() {
	    return (fistName != null ? "fistName:" + fistName + '\n' : "") +
			    (lastName != null ? "lastName:" + lastName + '\n' : "") +
			    (username != null ? "username:" + username + '\n' : "") +
			    (type != null ? "type:" + type + '\n' : "") +
			    "id:" + id;
    }
}
