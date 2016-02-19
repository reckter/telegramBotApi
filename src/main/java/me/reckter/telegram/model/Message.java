package me.reckter.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.reckter.telegram.ApplicationContextProvider;
import me.reckter.telegram.Telegram;
import me.reckter.telegram.requests.ParseMode;

import java.util.Optional;

/**
 * @author hannes
 */
public class Message extends BaseModel {


    @JsonProperty("message_id")
    public int id;

    public int date;

    @JsonProperty("from")
    public User user;


    public Chat chat;


    @JsonProperty("forward_from")
    public User forwardFrom;



    @JsonProperty("forward_date")
    public int forwardDate;


    @JsonProperty("reply_to_message")
    public Message replyTo;


    public String text;


    public Audio audio;


    public Document document;


    public PhotoSize[] photo;


    public Sticker sticker;


    public Video video;


    public Contact contact;


    public Location location;


    @JsonProperty("new_chat_participant")
    public User newChatParticipant;


    @JsonProperty("left_chat_participant")
    public User leftChatParticipant;


    @JsonProperty("new_chat_title")
    public String newTitle;


    @JsonProperty("new_chat_photo")
    public PhotoSize[] newChatPhoto;


    @JsonProperty("delete_chat_photo")
    public boolean deleteChatPhoto;


    @JsonProperty("group_chat_created")
    public boolean groupChatCreated;


    /**
     * reply to the message and cite it doing so.
     *
     * @param text the text to send
     * @return The Message send
     */
    public Message reply(String text) {
        return ApplicationContextProvider.getContext().getBean(Telegram.class).sendMessage(chat.id, text, ParseMode.NONE, Optional.empty(), Optional.of(id));
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getForwardFrom() {
        return forwardFrom;
    }

    public void setForwardFrom(User forwardFrom) {
        this.forwardFrom = forwardFrom;
    }

    public int getForwardDate() {
        return forwardDate;
    }

    public void setForwardDate(int forwardDate) {
        this.forwardDate = forwardDate;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public PhotoSize[] getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoSize[] photo) {
        this.photo = photo;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public void setSticker(Sticker sticker) {
        this.sticker = sticker;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getNewChatParticipant() {
        return newChatParticipant;
    }

    public void setNewChatParticipant(User newChatParticipant) {
        this.newChatParticipant = newChatParticipant;
    }

    public User getLeftChatParticipant() {
        return leftChatParticipant;
    }

    public void setLeftChatParticipant(User leftChatParticipant) {
        this.leftChatParticipant = leftChatParticipant;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public PhotoSize[] getNewChatPhoto() {
        return newChatPhoto;
    }

    public void setNewChatPhoto(PhotoSize[] newChatPhoto) {
        this.newChatPhoto = newChatPhoto;
    }

    public boolean isDeleteChatPhoto() {
        return deleteChatPhoto;
    }

    public void setDeleteChatPhoto(boolean deleteChatPhoto) {
        this.deleteChatPhoto = deleteChatPhoto;
    }

    public boolean isGroupChatCreated() {
        return groupChatCreated;
    }

    public void setGroupChatCreated(boolean groupChatCreated) {
        this.groupChatCreated = groupChatCreated;
    }
}
