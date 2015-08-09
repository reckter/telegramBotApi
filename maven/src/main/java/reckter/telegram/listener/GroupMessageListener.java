package reckter.telegram.listener;

import reckter.telegram.model.Message;

/**
 * @author hannes
 */
public interface GroupMessageListener {
    void groupMessageReceived(Message message);
}
