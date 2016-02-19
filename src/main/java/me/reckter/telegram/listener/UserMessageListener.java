package me.reckter.telegram.listener;

import me.reckter.telegram.model.Message;

/**
 * @author hannes
 */
public interface UserMessageListener {

    void onUserMessege(Message message);
}
