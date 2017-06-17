package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by hannesgudelhofer on 16.06.17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentRequest (

    @field:JsonProperty("chat_id")
    val id: String,

    val document: String,

    val caption: String? = null,


    @field:JsonProperty("disable_notification")
    val disableNotification: Boolean? = null,


    @field:JsonProperty("reply_to_message_id")
    val replyTo: Int? = null,

    @field:JsonProperty("reply_markup")
    val replyMarkup: ReplyMarkup? = null

)