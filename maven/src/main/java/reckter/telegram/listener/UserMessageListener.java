package reckter.telegram.listener;

import reckter.telegram.model.Message;

/**
 * @author hannes
 */
public interface UserMessageListener {

    void userMessegeReceived(Message message);
}
