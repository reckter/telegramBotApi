package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by hannes on 18/06/2016.
 */
class UpdateMessageRequest : MessageRequest() {


    @JsonProperty("message_id")
    var messageId: Long? = null

    @JsonProperty("inline_message_id")
    var inlineMessageId: String? = null
}
