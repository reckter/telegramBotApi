package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Hannes Güdelhöfer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class StickerRequest {

    @JsonProperty("chat_id")
    lateinit var chatId: String

    lateinit var sticker: String

    @JsonProperty("disable_notification")
    var disableNotifications: Boolean? = null

    @JsonProperty("reply_to_message_id")
    var replyTo: Long? = null

}
