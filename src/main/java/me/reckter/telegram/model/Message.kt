package me.reckter.telegram.model

import com.fasterxml.jackson.annotation.JsonProperty
import me.reckter.telegram.requests.ParseMode
import java.util.*

/**
 * @author hannes
 */
class Message : BaseModel() {


    @JsonProperty("message_id")
    var id: Int = 0

    var date: Int = 0

    @JsonProperty("from")
    lateinit var user: User


    lateinit var chat: Chat


    @JsonProperty("forward_from")
    var forwardFrom: User? = null


    @JsonProperty("forward_date")
    var forwardDate: Int = 0


    @JsonProperty("reply_to_message")
    var replyTo: Message? = null

    var entities: List<MessageEntity>? = null


    var text: String? = null


    var audio: Audio? = null


    var document: Document? = null


    var photo: Array<PhotoSize>? = null


    var sticker: Sticker? = null


    var video: Video? = null


    var contact: Contact? = null


    var location: Location? = null


    @JsonProperty("new_chat_participant")
    var newChatParticipant: User? = null


    @JsonProperty("left_chat_participant")
    var leftChatParticipant: User? = null


    @JsonProperty("new_chat_title")
    var newTitle: String? = null


    @JsonProperty("new_chat_photo")
    var newChatPhoto: Array<PhotoSize>? = null


    @JsonProperty("delete_chat_photo")
    var isDeleteChatPhoto: Boolean = false


    @JsonProperty("group_chat_created")
    var isGroupChatCreated: Boolean = false

    //in unix
    @JsonProperty("edit_date")
    var editDate: Int = 0


    /**
     * reply to the message and cite it doing so.

     * @param text the text to send
     * *
     * @return The Message send
     */
    fun reply(text: String): Message {
        return telegram.sendMessage(chat.id, text, ParseMode.NONE, Optional.empty<Boolean>(), Optional.empty<Boolean>(), Optional.of(id))
    }

    /**
     * Sends a message to the chat, does not cite the message reponded to.

     * @param text the text to send
     * *
     * @return The Message send
     */
    fun respond(text: String): Message {
        return chat.sendMessage(text)
    }


    val type: MessageType
        get() {
            if (editDate > 0) {
                return MessageType.EDITED
            }
            if (text != null) {
                if (text!!.startsWith("/")) {
                    return MessageType.COMMAND
                }
                return MessageType.MESSAGE
            }
            if (location != null) {
                return MessageType.LOCATION
            }
            if (video != null) {
                return MessageType.VIDEO
            }
            if (audio != null) {
                return MessageType.AUDIO
            }
            if (sticker != null) {
                return MessageType.STICKER
            }
            if (document != null) {
                return MessageType.DOCUMENT
            }
            if (newChatParticipant != null) {
                return MessageType.NEW_CHAT_PARTICIPANT
            }
            if (leftChatParticipant != null) {
                return MessageType.LEFT_CHAT_PARTICIPANT
            }

            return MessageType.MESSAGE
        }
}
