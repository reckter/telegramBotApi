package me.reckter.telegram.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author hannes
 */
class User : Chat() {

    @JsonProperty("first_name")
    var fistName: String? = null


    @JsonProperty("last_name")
    var lastName: String? = null


    var username: String? = null

    override fun toString(): String {
        return (if (fistName != null) "fistName:" + fistName + '\n' else "") +
                (if (lastName != null) "lastName:" + lastName + '\n' else "") +
                (if (username != null) "username:" + username + '\n' else "") +
                (if (type != null) "type:" + type + '\n' else "") +
                "id:" + id
    }
}
