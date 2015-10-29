package me.reckter.telegram.listener;

import me.reckter.telegram.model.Message;

/**
 * @author hannes
 */
public interface GroupMessageListener {
    void onGroupMessage(Message message);
}
