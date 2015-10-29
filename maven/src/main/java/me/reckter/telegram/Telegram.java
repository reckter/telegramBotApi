package me.reckter.telegram;

import me.reckter.json.Parser;
import me.reckter.telegram.listener.*;
import me.reckter.telegram.model.*;
import me.reckter.telegram.model.Error;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hannes
 */
public class Telegram {


    public static String API_URL = "https://api.telegram.org/bot";
    private long ignoreMessageBefore;

    public static class Endpoints {

        public static String UPDATE = "getUpdates";

        public static String MESSAGE_SEND = "sendMessage";
        public static String SEND_CHAT_ACTION = "sendChatAction";
    }

    Set<UserMessageListener> userMessageListeners = new HashSet<>();
    Set<GroupMessageListener> groupMessageListeners = new HashSet<>();

    Set<UserCommandListener> userCommandListeners = new HashSet<>();
    Set<GroupCommandListener> groupCommandListeners = new HashSet<>();

    Provider provider;

    String apiKey;

    Parser parser;

    Optional<String> replyMarkup = Optional.empty();

    public Telegram(String apiKey) {
        this.apiKey = apiKey;
        this.parser = new Parser(this);
        this.parser.staticAddParsables();

        this.provider = new PullProvider(1000, this);
        this.provider.setApiKey(apiKey);
        this.provider.start();
        this.ignoreMessageBefore = System.currentTimeMillis() / 1000;
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
        new Thread(() -> {

            if (update.message.date < ignoreMessageBefore) {
                return;
            }

            if (update.message.chat instanceof User) {
                if (update.message.text.startsWith("/")) {
                    userCommandListeners.forEach(listener -> listener.onUserCommand(update.message));
                } else {
                    userMessageListeners.forEach(listener -> listener.onUserMessege(update.message));
                }
            } else {
                if (update.message.text.startsWith("/")) {
                    groupCommandListeners.forEach(listener -> listener.onGroupCommand(update.message));
                } else {
                    groupMessageListeners.forEach(listener -> listener.onGroupMessage(update.message));
                }
            }

        }).run();
    }

    public Object getUpdate() {
        return apiRequest(Endpoints.UPDATE, new HashMap<>());
    }

    public Object getUpdate(int offset, int limit, int timeout) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("offset", String.valueOf(offset));
        arguments.put("limit", String.valueOf(limit));
        arguments.put("timeout", String.valueOf(timeout));
        return apiRequest(Endpoints.UPDATE, arguments);
    }

    public void setReplyMarkupToHideKeyboard(boolean selective) {
        replyMarkup = Optional.of("{force_reply:true,selective:" + selective + "}");
    }

    public void setReplyMarkupToCustomKeyboard(String[][] keys, boolean selective) {
        setReplyMarkupToCustomKeyboard(keys, Optional.<Boolean>empty(), Optional.<Boolean>empty(), selective);
    }

    public void setReplyMarkupToCustomKeyboard(String[][] keys, Optional<Boolean> resizeKeyboard, Optional<Boolean> oneTimeKeyboard, boolean selective) {
        StringBuilder reply = new StringBuilder("{\"keyboard\":[");
        for (String[] row : keys) {
            reply.append("[");
            for (String key : row) {
                reply.append("\"").append(key).append("\",");
            }
            reply.deleteCharAt(reply.length() - 1).append("],");
        }
        reply.deleteCharAt(reply.length() - 1).append("]");
        if (resizeKeyboard.isPresent()) {
            reply.append(",\"resize_keyboard\":").append(resizeKeyboard.get());
        }
        if (oneTimeKeyboard.isPresent()) {
            reply.append(",\"one_time_keyboard\":").append(oneTimeKeyboard.get());
        }
        reply.append(",\"selective\":").append(selective);
        reply.append("}");
        replyMarkup = Optional.of(reply.toString());
    }

    public void setReplyMarkupToForceReply(boolean selective) {
        replyMarkup = Optional.of("{force_reply:true,selective:" + selective + "}");
    }

    public void sendChatAction(int chatId, ChatAction action) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("chat_id", String.valueOf(chatId));
        arguments.put("action", String.valueOf(action));
        apiRequest(Endpoints.SEND_CHAT_ACTION, arguments, false);
    }

    public Message sendMessage(int chatId, String text) {
        return sendMessage(chatId, text, Optional.<String>empty(), Optional.<Boolean>empty(), Optional.<Integer>empty());
    }

    /**
     * @param chatId             chat id to send to
     * @param text               text t send
     * @param disableWebPageView if web page view is dissabled
     * @param replyToMessageId   if this is a resopnd set to resondId
     * @return the messag send.
     */
    public Message sendMessage(int chatId, String text, Optional<String> parseView, Optional<Boolean> disableWebPageView, Optional<Integer> replyToMessageId) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("chat_id", String.valueOf(chatId));
        arguments.put("text", text);
        arguments.put("disable_web_page_view", String.valueOf(disableWebPageView.orElse(false)));
        arguments.put("parse_mode", String.valueOf(parseView.orElse("Markdown")));

        if (replyToMessageId.isPresent()) {
            arguments.put("reply_to_message_id", String.valueOf(replyToMessageId.get()));
        }

        if (replyMarkup.isPresent()) {
            arguments.put("reply_markup", replyMarkup.get());
            replyMarkup = Optional.empty();
        }


        Object response = (apiRequest(Endpoints.MESSAGE_SEND, arguments));
        if (response instanceof Error) {
            if (((Error) response).errorCode == 400 && ((Error) response).description.equals("Error: Message is too long")) {
                String text1 = text.substring(0, text.length() / 2);
                String text2 = text.substring(text1.length(), text.length());
                text1 += text2.split("\n", 2)[0];
                text2 = text2.split("\n", 2)[1];
                System.out.println("splitting message of length " + text.length() + " into " + text1.length() + " and " + text2.length());

                sendMessage(chatId, text1, parseView, disableWebPageView, replyToMessageId);
                return sendMessage(chatId, text2, parseView, disableWebPageView, replyToMessageId);

            } else if(((Error) response).errorCode == 400 && ((Error) response).description.startsWith("Error: Bad Request: can't parse message text:")) {
                //System.out.println("Markdown parsing is wrong! Please have a look at '" + text + "'");
                return sendMessage(chatId, text, Optional.of(""), disableWebPageView, replyToMessageId);
            } else {
                throw new RuntimeException("error sending message! " + ((Error) response).description + " (" + ((Error) response).errorCode + ")");
            }
        } else {
            return (Message) ((Response) response).result[0];
        }
    }

    private Object apiRequest(String requestPoint, Map<String, String> arguments) {
        return apiRequest(requestPoint, arguments, true);
    }

    private Object apiRequest(String requestPoint, Map<String, String> arguments, Boolean parseResponse) {
        CloseableHttpClient httpClient = HttpClients.createDefault();


        List<NameValuePair> formparams = arguments.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost request = new HttpPost(Telegram.API_URL + apiKey + "/" + requestPoint);

        request.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String out = rd.lines().collect(Collectors.joining());
            if (parseResponse) {
                Object obj = parser.parse(out);
                if (obj instanceof Error) {
                    System.err.println("errNo: " + ((Error) obj).errorCode + " " + ((Error) obj).description);
                }
                return obj;
            } else {
                return out;
            }


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("io exception while reading api ", e);
        }
    }

    public long getIgnoreMessageBefore() {
        return ignoreMessageBefore;
    }

    public Telegram setIgnoreMessageBefore(long ignoreMessageBefore) {
        this.ignoreMessageBefore = ignoreMessageBefore;
        return this;
    }
}
