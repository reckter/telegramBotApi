package reckter.telegram.listener;

import reckter.telegram.model.Message;

/**
 * @author hannes
 */
@FunctionalInterface
public interface MessageListener extends UserMessageListener, GroupMessageListener {

    void messageReceived(Message message);

    @Override
    default void groupMessageReceived(Message message) {
        messageReceived(message);
    }

    @Override
    default void userMessegeReceived(Message message) {
        messageReceived(message);
    }
}
