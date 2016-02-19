package me.reckter.telegram.listener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author hannes
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OnMessage {
    String regex() default ".*";

    String[] matchingGroup() default {};

    ChatType type() default ChatType.ALL;

}
