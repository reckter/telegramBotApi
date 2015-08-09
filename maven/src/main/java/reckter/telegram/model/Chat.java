package reckter.telegram.model;

/**
 * @author hannes
 */
public abstract class Chat extends BaseModel{
    int id;


    public void sendAction(ChatAction action) {
        telegram.sendChatAction(id, action);
    }

    public Message sendMessage(String text) {
        return telegram.sendMessage(id, text);
    }
}
