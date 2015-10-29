package me.reckter.telegram.listener;

import me.reckter.telegram.model.GroupChat;
import me.reckter.telegram.model.Message;
import me.reckter.telegram.model.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author hannes
 */
public class ReflectionListener implements GroupCommandListener, GroupMessageListener, UserMessageListener, UserCommandListener {

    List<Method> commandListener = new ArrayList<>();
    List<Method> messageListener = new ArrayList<>();

    Object listenerObject;


    public ReflectionListener(Object listener) {
        this.listenerObject = listener;
        parse();
    }

    private void parse() {
        Method[] methods = listenerObject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(OnCommand.class)) {
                commandListener.add(method);
            }
            if (method.isAnnotationPresent(OnMessage.class)) {
                messageListener.add(method);
            }
        }
    }

    private void invoke(Message message) {
        List<Method> methods;
        if (message.text.startsWith("/")) {
            final boolean[] foundCommand = {false};
            commandListener.stream().filter(method ->
                        Stream.of(method.getDeclaredAnnotation(OnCommand.class).value())
                                .anyMatch(name -> message.text.startsWith("/" + name)))
                    .forEach(method -> {
                        OnCommand onCommand = method.getDeclaredAnnotation(OnCommand.class);
                        if (onCommand.type() == ChatType.ALL
                                || onCommand.type() == ChatType.GROUP && message.chat instanceof GroupChat
                                || onCommand.type() == ChatType.USER && message.chat instanceof User) {

                            foundCommand[0] = true;
                            try {
                                method.setAccessible(true);
                                method.invoke(listenerObject, message);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                System.err.println("There seems to be a Problem with the Handler!");
                                System.err.println("Exception thrown: " +  e.getCause().getMessage());
                                e.getCause().printStackTrace();
                            }
                        }
                    });
            if (!foundCommand[0]) {
                message.reply("did not find this command!");
            }


        } else {
            messageListener.stream().forEach(method -> {
                OnMessage onMessage = method.getDeclaredAnnotation(OnMessage.class);

                Matcher matcher = Pattern.compile(onMessage.regex()).matcher(message.text);

                if (!matcher.matches()) {
                    return;
                }

                Map<String, String> groups = new HashMap<>();
                for (String group : onMessage.matchingGroup()) {
                    groups.put(group, matcher.group(group));
                }
                try {
                    method.invoke(listenerObject, message, groups);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    @Override
    public void onUserCommand(Message message) {
        invoke(message);
    }

    @Override
    public void onGroupMessage(Message message) {
        invoke(message);
    }

    @Override
    public void onUserMessege(Message message) {
        invoke(message);
    }

    @Override
    public void onGroupCommand(Message message) {
        invoke(message);
    }
}
