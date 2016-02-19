package me.reckter.telegram.listener;

import me.reckter.telegram.model.Message;

/**
 * @author hannes
 */
@FunctionalInterface
public interface GroupCommandListener {
    boolean onGroupCommand(Message message);
}
