package me.reckter.telegram.listener;

/**
 * @author hannes
 */

import me.reckter.telegram.model.Message;

@FunctionalInterface
public interface UserCommandListener {
    void onUserCommand(Message message);
}
