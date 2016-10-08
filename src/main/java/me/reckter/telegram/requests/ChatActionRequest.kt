package me.reckter.telegram.requests

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by hannes on 18.02.16.
 */
class ChatActionRequest {

    lateinit var action: ChatAction

    @JsonProperty("chat_id")
    lateinit var id: String
}
