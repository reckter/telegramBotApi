package me.reckter.telegram.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author hannes
 */
class Contact {

    @field:JsonProperty("phone_number")
    lateinit var phoneNumber: String

    @field:JsonProperty("first_name")
    lateinit var  firstName: String

    @field:JsonProperty("last_name")
    lateinit var  lastName: String

    @field:JsonProperty("user_id")
    val userId: Long? = null
}
