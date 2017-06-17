package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by hannesgudelhofer on 17.06.17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateCaptionRequest(


        @field:JsonProperty("chat_id")
        var chatId: String? = null,

        @field:JsonProperty("message_id")
        var messageId: Int? = null,

        @field:JsonProperty("inline_message_id")
        var inlineMessageId: String? = null,

        val caption: String? = null,

        @field:JsonProperty("reply_markup")
        val replyMarkup: ReplyMarkup? = null
)