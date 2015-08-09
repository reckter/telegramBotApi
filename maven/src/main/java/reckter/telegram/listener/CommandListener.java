package reckter.telegram.listener;

import reckter.telegram.model.Message;

/**
 * @author hannes
 */
@FunctionalInterface
public interface  CommandListener {
    void onCommand(Message message);
}
