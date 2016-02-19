package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hannes
 */
public class User extends Chat {

    @JsonProperty("first_name")
    public String fistName;


    @JsonProperty("last_name")
    public String lastName;


    public String username;

    @Override
    public String toString() {
	    return (fistName != null ? "fistName:" + fistName + '\n' : "") +
			    (lastName != null ? "lastName:" + lastName + '\n' : "") +
			    (username != null ? "username:" + username + '\n' : "") +
			    (type != null ? "type:" + type + '\n' : "") +
			    "id:" + id;
    }

    public String getFistName() {
        return fistName;
    }

    public void setFistName(String fistName) {
        this.fistName = fistName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
