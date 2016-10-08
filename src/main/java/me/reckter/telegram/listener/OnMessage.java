package me.reckter.telegram.listener;

import me.reckter.telegram.model.MessageType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author hannes
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OnMessage {
    public MessageType[] value() default MessageType.MESSAGE;
}
