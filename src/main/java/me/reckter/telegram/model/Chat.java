package me.reckter.telegram.model;


import me.reckter.telegram.ApplicationContextProvider;
import me.reckter.telegram.Telegram;
import me.reckter.telegram.requests.ChatAction;

/**
 * @author hannes
 */
public class Chat extends BaseModel {

    public long id;

    public String type;

    public void sendAction(ChatAction action) {
        ApplicationContextProvider.getContext().getBean(Telegram.class).sendChatAction(id, action);
    }

    public Message sendMessage(String text) {
        return ApplicationContextProvider.getContext().getBean(Telegram.class).sendMessage(id, text);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
