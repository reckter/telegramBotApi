package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by hannes on 18.02.16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class LocationRequest {

    @JsonProperty("chat_id")
    lateinit var id: String

    var latitude: Float = 0.toFloat()

    var longitude: Float = 0.toFloat()

    @JsonProperty("disable_notification")
    var disableNotification: Boolean? = null

    @JsonProperty("reply_to_message_id")
    var replyTo: Int? = null
}
