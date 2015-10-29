package me.reckter.telegram.model;

import me.reckter.json.JsonName;
import me.reckter.json.JsonOptional;

import java.util.Optional;

/**
 * @author hannes
 */
public class Message extends BaseModel {


    @JsonName("message_id")
    public int id;

    public int date;

    @JsonName("from")
    public User user;

    @JsonOptional
    public Chat chat;

    @JsonOptional
    @JsonName("forward_from")
    public User forwardFrom;


    @JsonOptional
    @JsonName("forward_date")
    public int forwardDate;

    @JsonOptional
    @JsonName("reply_to_message")
    public Message replyTo;

    @JsonOptional
    public String text;

    @JsonOptional
    public Audio audio;

    @JsonOptional
    public Document document;

    @JsonOptional
    public PhotoSize[] photo;

    @JsonOptional
    public Sticker sticker;

    @JsonOptional
    public Video video;

    @JsonOptional
    public Contact contact;

    @JsonOptional
    public Location location;

    @JsonOptional
    @JsonName("new_chat_participant")
    public User newChatParticipant;

    @JsonOptional
    @JsonName("left_chat_participant")
    public User leftChatParticipant;

    @JsonOptional
    @JsonName("new_chat_title")
    public String newTitle;

    @JsonOptional
    @JsonName("new_chat_photo")
    public PhotoSize[] newChatPhoto;

    @JsonOptional
    @JsonName("delete_chat_photo")
    public boolean deleteChatPhoto;

    @JsonOptional
    @JsonName("group_chat_created")
    public boolean groupChatCreated;


    /**
     * reply to the message and cite it doing so.
     *
     * @param text the text to send
     * @return The Message send
     */
    public Message reply(String text) {
        return telegram.sendMessage(chat.id, text, Optional.empty(), Optional.empty(), Optional.of(id));
    }

    /**
     * Sends a message to the chat, does not cite the message reponded to.
     *
     * @param text the text to send
     * @return The Message send
     */
    public Message respond(String text) {
        return chat.sendMessage(text);
    }


}
