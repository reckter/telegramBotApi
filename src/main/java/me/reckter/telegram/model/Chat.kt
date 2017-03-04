package me.reckter.telegram.model


import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import me.reckter.telegram.requests.ChatAction

/**
 * @author hannes
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = User::class)
@JsonSubTypes(JsonSubTypes.Type(User::class, name = "private"),
        JsonSubTypes.Type(GroupChat::class, name = "group"),
        JsonSubTypes.Type(GroupChat::class, name = "supergroup"),
        JsonSubTypes.Type(Channel::class, name = "channel"))
open class Chat : BaseModel() {

    lateinit var id: String

    open var type: String? = null

    fun sendAction(action: ChatAction) {
        telegram.sendChatAction(id, action)
    }

    fun sendMessage(text: String): Message {
        return telegram.sendMessage(id, text)
    }
}
