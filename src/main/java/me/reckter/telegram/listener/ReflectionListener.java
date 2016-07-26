package me.reckter.telegram.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.reckter.telegram.model.GroupChat;
import me.reckter.telegram.model.Message;
import me.reckter.telegram.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger Log = LoggerFactory.getLogger(ReflectionListener.class)

    List<Method> commandListener = new ArrayList<>();
    List<Method> messageListener = new ArrayList<>();
    List<Method> locationListener = new ArrayList<>();
    List<Method> userCreateListener = new ArrayList<>();
    List<Method> userLeaveListener = new ArrayList<>();

    Object listenerObject;


    public ReflectionListener(Object listener) {
        this.listenerObject = listener;
        parse();
    }

    private void parse() {
        Method[] methods = listenerObject.getClass().getMethods();
        for(Method method : methods) {
            if(method.isAnnotationPresent(OnCommand.class)) {
                commandListener.add(method);
            }
            if(method.isAnnotationPresent(OnMessage.class)) {
                messageListener.add(method);
            }
            if(method.isAnnotationPresent(OnLocation.class)) {
                locationListener.add(method);
            }
            if(method.isAnnotationPresent(OnUserJoin.class)) {
                userCreateListener.add(method);
            }
            if(method.isAnnotationPresent(OnUserLeave.class)) {
                userLeaveListener.add(method);
            }
        }
    }


    private List<String> getArguments(String text) {

        text = text.substring(1);
        text = text + " ";
        String[] split = text.split(" ", 2);
        text = split[0].replace("_", " ") + " " + split[1];

        List<String> ret = new ArrayList<>();

        int argumentStartedAt = 0;
        boolean insideQoutes = false;
        for(int i = 0; i < text.length(); i++) {

            if(text.charAt(i) == '\\') {
                i++;
                continue;
            }

            if(text.charAt(i) == '"') {
                insideQoutes = !insideQoutes;
            }

            if(!insideQoutes && text.charAt(i) == ' ') {
                ret.add(text.substring(argumentStartedAt, i));
                argumentStartedAt = i + 1;
            }
        }
        return ret;
    }


    private void invokeSimpleHandler(Method method, Message message) {
        method.setAccessible(true);
        try {
            method.invoke(listenerObject, message);
        } catch(IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("There seems to be a Problem with the Handler!", e);
        }
    }

    private boolean invoke(Message message) {
        List<Method> methods;

        if(message.getLocation() != null) {
            locationListener.stream().forEach(method -> invokeSimpleHandler(method, message));
        } else if(message.getNewChatParticipant() != null) {
            userCreateListener.stream().forEach(method -> invokeSimpleHandler(method, message));
        } else if(message.getLeftChatParticipant() != null) {
            userLeaveListener.stream().forEach(method -> invokeSimpleHandler(method, message));
        } else if(message.text != null && message.text.startsWith("/")) {

            List<String> arguments = getArguments(message.text);
            String commandCalled = arguments.get(0);


            final boolean[] foundCommand = {false};
            commandListener.stream().filter(method ->
                                                    Stream.of(method.getDeclaredAnnotation(OnCommand.class).value())
                                                            .anyMatch(name -> name.toLowerCase().equals(commandCalled.toLowerCase())))
                    .forEach(method -> {
                        OnCommand onCommand = method.getDeclaredAnnotation(OnCommand.class);
                        if(onCommand.type() == ChatType.ALL
                                || onCommand.type() == ChatType.GROUP && message.chat instanceof GroupChat
                                || onCommand.type() == ChatType.USER && message.chat instanceof User) {

                            foundCommand[0] = true;
                            try {
                                method.setAccessible(true);
                                method.invoke(listenerObject, message, arguments);
                            } catch(IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("There seems to be a Problem with the Handler!", e);
                            }
                        }
                    });
            if(!foundCommand[0]) {
                return false;
            }


        } else if(message.text != null) {
            messageListener.stream().forEach(method -> {
                OnMessage onMessage = method.getDeclaredAnnotation(OnMessage.class);

                Matcher matcher = Pattern.compile(onMessage.regex()).matcher(message.text);

                if(!matcher.matches()) {
                    return;
                }

                Map<String, String> groups = new HashMap<>();
                for(String group : onMessage.matchingGroup()) {
                    groups.put(group, matcher.group(group));
                }
                try {
                    method.invoke(listenerObject, message, groups);
                } catch(IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Log.info("could not find suitable action for " + objectMapper.writeValueAsString(message));
            } catch(JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean onUserCommand(Message message) {
        return invoke(message);
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
    public boolean onGroupCommand(Message message) {
        return invoke(message);
    }
}
