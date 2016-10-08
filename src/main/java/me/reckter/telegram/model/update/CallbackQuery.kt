package me.reckter.telegram.model.update

import com.fasterxml.jackson.annotation.JsonProperty
import me.reckter.telegram.model.Message
import me.reckter.telegram.model.User

/**
 * @author Hannes Güdelhöfer
 */
class CallbackQuery {
    lateinit var id: String

    lateinit var from: User

    var message: Message? = null

    @JsonProperty("inline_message_id")
    var inlineMessageId: String? = null

    var data: String? = null

    @JsonProperty("chat_instance")
    lateinit var chatId: String

    @JsonProperty("game_short_name")
    var gameShortName: String? = null
}
