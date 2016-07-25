package me.reckter.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.reckter.telegram.listener.*;
import me.reckter.telegram.model.Error;
import me.reckter.telegram.model.*;
import me.reckter.telegram.model.update.Update;
import me.reckter.telegram.requests.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author hannes
 */
public class Telegram {

    private static Logger LOG = LoggerFactory.getLogger(Telegram.class);

    private static Telegram errorTelegramBot;

    private TelegramClient telegramClient;

    ObjectMapper mapper = new ObjectMapper();


    public static String API_URL = "https://api.telegram.org/bot";
    private long ignoreMessageBefore;

    public static class Endpoints {

        public static String UPDATE = "getUpdates";

        public static String SEND_MESSAGE = "sendMessage";
        public static String EDIT_MESSAGE_TEXT = "editMessageText";

        public static String SEND_LOCATION = "sendLocation";
        public static String GET_ME = "getMe";
        public static String SEND_CHAT_ACTION = "sendChatAction";
        public static String SET_WEBHOOK = "setWebhook";

        public static String SEND_STICKER = "sendSticker";
    }

    Set<UserMessageListener> userMessageListeners = new HashSet<>();
    Set<GroupMessageListener> groupMessageListeners = new HashSet<>();

    Set<UserCommandListener> userCommandListeners = new HashSet<>();
    Set<GroupCommandListener> groupCommandListeners = new HashSet<>();

    Provider provider;

    int adminChat;

    String apiKey;

    Optional<String> replyMarkup = Optional.empty();

    public Telegram(String apiKey, boolean startPulling) {
        this.apiKey = apiKey;

        telegramClient = new TelegramClient(API_URL, apiKey);
        this.provider = new PullProvider(1000, this);
        this.provider.setApiKey(apiKey);
        if(startPulling) {
            this.provider.start();
        }

        this.ignoreMessageBefore = System.currentTimeMillis() / 1000;

    }


    public Telegram(String apiKey, int adminChat) {
        this(apiKey, true);
        this.adminChat = adminChat;

        sendMessage(adminChat, "booted\n" + getMe());
    }

    public Telegram(String apiKey, int adminChat, boolean startPulling) {
        this(apiKey, startPulling);
        this.adminChat = adminChat;

        sendMessage(adminChat, "booted\n" + getMe());
    }


    public Telegram(String apiKey, int adminChat, String errorApiKey) {
        this(apiKey, adminChat);
        errorTelegramBot = new Telegram(errorApiKey, false);

    }

    public Telegram(String apiKey, int adminChat, String errorApiKey, boolean startPulling) {
        this(apiKey, adminChat, startPulling);
        errorTelegramBot = new Telegram(errorApiKey, false);
    }

    public void sendExceptionErrorMessage(Exception e) {
        sendExceptionErrorMessage(e, "");
    }

    public void sendExceptionErrorMessage(Exception e, String additionalMessage) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        sendErrorMessage(!additionalMessage.equals("") ? (additionalMessage + "\n\n" + result.toString()) : result.toString());
    }

    public void sendErrorMessage(String message) {
        Telegram telegramToUse = errorTelegramBot != null ? errorTelegramBot : this;

        message = "Error in Bot '" + getMe().username + "':\n" + message;
        telegramToUse.sendMessage(adminChat, message, ParseMode.NONE, Optional.empty(), Optional.empty(), Optional.empty());

    }


    public void addUserMessageListeners(UserMessageListener userMessageListener) {
        userMessageListeners.add(userMessageListener);
    }


    public void addUserCommandListeners(UserCommandListener userCommandListener) {
        userCommandListeners.add(userCommandListener);
    }

    public void addGroupMessageListeners(GroupMessageListener groupMessageListener) {
        groupMessageListeners.add(groupMessageListener);
    }

    public void addGroupCommandListeners(GroupCommandListener groupCommandListener) {
        groupCommandListeners.add(groupCommandListener);
    }

    public void addListener(Object listener) {
        ReflectionListener reflectionListener = new ReflectionListener(listener);

        addUserMessageListeners(reflectionListener);
        addUserCommandListeners(reflectionListener);

        addGroupMessageListeners(reflectionListener);
        addGroupCommandListeners(reflectionListener);
    }


    public void acceptUpdate(Update update) {
        try {

            if(update.message.date < ignoreMessageBefore) {
                return;
            }
            if(update.editedMessage != null) {
                //TODO
                LOG.info("got an eddited message. ignoring them for now");
                return;
            }

            if(update.message.chat instanceof User) {
                if(update.message.text != null && update.message.text.startsWith("/")) {
                    boolean foundCommand = false;
                    for(UserCommandListener userCommandListener : userCommandListeners) {
                        foundCommand |= userCommandListener.onUserCommand(update.message);
                    }
                    if(!foundCommand) {
                        update.message.reply("Did not find this command");
                    }
                } else {
                    userMessageListeners.forEach(listener -> listener.onUserMessege(update.message));
                }
            } else {
                if(update.message.text != null && update.message.text.startsWith("/")) {
                    boolean foundCommand = false;
                    for(GroupCommandListener groupCommandListener : groupCommandListeners) {
                        foundCommand |= groupCommandListener.onGroupCommand(update.message);
                    }
                    if(!foundCommand) {
                        update.message.reply("Did not find this command");
                    }
                } else {
                    groupMessageListeners.forEach(listener -> listener.onGroupMessage(update.message));
                }
            }

        } catch(Exception e) {
            if(update.message == null) {
                LOG.error("update.message was null!");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    LOG.error(mapper.writeValueAsString(update));
                } catch(JsonProcessingException e1) {
                    e1.printStackTrace();
                }
            }
            update.message.reply("Sorry got a hick up. Try again later.");
            sendExceptionErrorMessage(e);
        }
    }


    public List<Update> getUpdate() {
        return getUpdates(-1, -1, -1);
    }

    public List<Update> getUpdates(long offset, long limit, long timeout) {
        UpdateRequest updateRequest = new UpdateRequest();
        if(offset != -1) {
            updateRequest.setOffset(offset);
        }

        if(limit != -1) {
            updateRequest.setLimit(limit);
        }

        if(timeout != -1) {
            updateRequest.setTimeout(timeout);
        }
        return telegramClient
                .requestFor(new ParameterizedTypeReference<Response<List<Update>>>() {
                })
                .uri(Endpoints.UPDATE)
                .payload(updateRequest)
                .method(HttpMethod.POST)
                .request()
                .getBody()
                .getResult();
    }


    public User getMe() {
        return telegramClient
                .requestFor(new ParameterizedTypeReference<Response<User>>() {
                })
                .uri(Endpoints.GET_ME)
                .method(HttpMethod.GET)
                .request()
                .getBody()
                .getResult();

    }

    public void setReplyMarkupToHideKeyboard(boolean selective) {
        replyMarkup = Optional.of("{force_reply:true,selective:" + selective + "}");
    }

    public void setReplyMarkupToCustomKeyboard(String[][] keys, boolean selective) {
        setReplyMarkupToCustomKeyboard(keys, Optional.<Boolean>empty(), Optional.<Boolean>empty(), selective);
    }

    public void setReplyMarkupToCustomKeyboard(String[][] keys, Optional<Boolean> resizeKeyboard, Optional<Boolean> oneTimeKeyboard, boolean selective) {
        StringBuilder reply = new StringBuilder("{\"keyboard\":[");
        for(String[] row : keys) {
            reply.append("[");
            for(String key : row) {
                reply.append("\"").append(key).append("\",");
            }
            reply.deleteCharAt(reply.length() - 1).append("],");
        }
        reply.deleteCharAt(reply.length() - 1).append("]");
        if(resizeKeyboard.isPresent()) {
            reply.append(",\"resize_keyboard\":").append(resizeKeyboard.get());
        }
        if(oneTimeKeyboard.isPresent()) {
            reply.append(",\"one_time_keyboard\":").append(oneTimeKeyboard.get());
        }
        reply.append(",\"selective\":").append(selective);
        reply.append("}");
        replyMarkup = Optional.of(reply.toString());
    }

    public void setReplyMarkupToForceReply(boolean selective) {
        replyMarkup = Optional.of("{force_reply:true,selective:" + selective + "}");
    }

    public void sendChatAction(long chatId, ChatAction action) {

        ChatActionRequest chatActionRequest = new ChatActionRequest();
        chatActionRequest.setId(chatId);
        chatActionRequest.setAction(action);

        telegramClient
                .request()
                .uri(Endpoints.SEND_CHAT_ACTION)
                .payload(chatActionRequest)
                .method(HttpMethod.POST)
                .request();
    }

    public Message sendSticker(long chatId, ClassPathResource file) {
        return sendSticker(chatId, file, Optional.empty(), Optional.empty());
    }

    public Message sendSticker(long chatId, ClassPathResource file, Optional<Boolean> disableNotifications, Optional<Integer> replyToMessageId) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("chat_id", chatId);
        parts.add("sticker", file);
        if(disableNotifications.isPresent()) {
            parts.add("disable_notification", disableNotifications.get());
        }
        if(replyToMessageId.isPresent()) {
            parts.add("reply_to_message_id", replyToMessageId.get());
        }

        return telegramClient
                .requestFor(new ParameterizedTypeReference<Response<Message>>() {})
                .method(HttpMethod.POST)
                .uri(Endpoints.SEND_STICKER)
                .fileUpload()
                .payload(parts)
                .request()
                .getBody()
                .getResult();
    }

    public Message sendLocation(long chatId, Location location) {
        return sendLocation(chatId, location, Optional.empty());
    }

    public Message sendLocation(long chatId, Location location, Optional<Boolean> disableNotification) {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setId(chatId);
        locationRequest.setLatitude(location.getLatitude());
        locationRequest.setLongitude(location.getLongitude());

        if(disableNotification.isPresent()) {
            locationRequest.disableNotification = disableNotification.get();
        }

        return telegramClient
                .requestFor(new ParameterizedTypeReference<Response<Message>>() {})
                .uri(Endpoints.SEND_LOCATION)
                .method(HttpMethod.POST)
                .payload(locationRequest)
                .request()
                .getBody()
                .getResult();
    }

    public Message sendMessage(long chatId, String text) {
        return sendMessage(chatId, text, ParseMode.NONE, Optional.empty(), Optional.<Boolean>empty(), Optional.<Integer>empty());
    }


    public Message sendMessage(MessageRequest messageRequest) {

        try {
            return telegramClient
                    .requestFor(new ParameterizedTypeReference<Response<Message>>() {
                    })
                    .method(HttpMethod.POST)
                    .payload(messageRequest)
                    .uri(Endpoints.SEND_MESSAGE)
                    .request()
                    .getBody()
                    .getResult();
        } catch(HttpClientErrorException e) {
            try {
                Error error = mapper.readValue(e.getResponseBodyAsByteArray(), Error.class);
                if(error.errorCode == 400 && error.description.contains("Message is too long")) {
                    MessageRequest firstHalf = new MessageRequest(messageRequest);
                    MessageRequest secondHalf = new MessageRequest(messageRequest);
                    String text = messageRequest.getText();
                    String text1 = text.substring(0, text.length() / 2);
                    String text2 = text.substring(text1.length(), text.length());

                    text1 += text2.split("\n", 2)[0];
                    text2 = text2.split("\n", 2)[1];

                    System.out.println("splitting message of length " + text.length() + " into " + text1.length() + " and " + text2.length());

                    firstHalf.setText(text1);
                    secondHalf.setText(text2);

                    sendMessage(firstHalf);
                    return sendMessage(secondHalf);

                } else if(error.errorCode == 400 && error.description.startsWith("[Error]: Bad Request: Can't parse message text:")) {
                    //System.out.println("Markdown parsing is wrong! Please have a look at '" + text + "'")
                    messageRequest.setParseMode(ParseMode.NONE);
                    return sendMessage(messageRequest);
                } else {
                    throw new RuntimeException("error sending message! " + error.description + " (" + error.errorCode + ")");
                }
            } catch(IOException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        }

    }

    /**
     * @param chatId             chat id to send to
     * @param text               text t send
     * @param disableWebPageView if web page view is dissabled
     * @param replyToMessageId   if this is a resopnd set to resondId
     * @return the messag send.
     */
    public Message sendMessage(long chatId, String text, ParseMode parseMode, Optional<Boolean> disableNotification, Optional<Boolean> disableWebPageView, Optional<Integer> replyToMessageId) {

        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setId(chatId);
        messageRequest.setText(text);
        messageRequest.setParseMode(parseMode);
        if(disableNotification.isPresent()) {
            messageRequest.disableNotification = disableNotification.get();
        }
        if(disableWebPageView.isPresent()) {
            messageRequest.setDisableWebPagePreview(disableWebPageView.get());
        }
        if(replyToMessageId.isPresent()) {
            messageRequest.setReplyTo(replyToMessageId.get());
        }
        return sendMessage(messageRequest);
    }

    public void editMessage(long chatId, long messageId, String text) {
        editMessage(chatId, messageId, text, ParseMode.NONE, Optional.empty());
    }

    public void editMessage(Message message) {
        editMessage(message, ParseMode.NONE, Optional.empty());
    }

    public void editMessage(Message message, ParseMode parseMode, Optional<Boolean> disableWebPageView) {
        editMessage(message.chat.id, message.id, message.text, parseMode, disableWebPageView);
    }

    public void editMessage(long chatId, long messageId, String text, ParseMode parseMode, Optional<Boolean> disableWebPageView) {

        UpdateMessageRequest messageRequest = new UpdateMessageRequest();

        messageRequest.setId(chatId);
        messageRequest.setMessageId(messageId);
        messageRequest.setText(text);
        messageRequest.setParseMode(parseMode);
        if(disableWebPageView.isPresent()) {
            messageRequest.setDisableWebPagePreview(disableWebPageView.get());
        }

        telegramClient
                .request()
                .method(HttpMethod.POST)
                .payload(messageRequest)
                .uri(Endpoints.EDIT_MESSAGE_TEXT)
                .request();

    }

    public long getIgnoreMessageBefore() {
        return ignoreMessageBefore;
    }

    public Telegram setIgnoreMessageBefore(long ignoreMessageBefore) {
        this.ignoreMessageBefore = ignoreMessageBefore;
        return this;
    }

    public int getAdminChat() {
        return adminChat;
    }

    public Telegram getErrorTelegramBot() {
        return errorTelegramBot;
    }

    public Provider getProvider() {
        return provider;
    }
}
