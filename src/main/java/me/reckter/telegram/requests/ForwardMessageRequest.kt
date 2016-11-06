package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class ForwardMessageRequest {

    @JsonProperty("chat_id")
    lateinit var toChat: String

    @JsonProperty("from_chat_id")
    lateinit var fromChat: String

    @JsonProperty("disable_notification")
    var disableNotification: Boolean? = null

    @JsonProperty("message_id")
    var messageId: Int? = null
}