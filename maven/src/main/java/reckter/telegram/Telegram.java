package reckter.telegram;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import reckter.json.Parser;
import reckter.telegram.listener.UserMessageListener;
import reckter.telegram.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hannes
 */
public class Telegram {


    public static String API_URL = "https://api.telegram.org/";
    private long ignoreMessageBefore;

    public static class Endpoints {

        public static String UPDATE = "getUpdates";

        public static String MESSAGE_SEND = "sendMessage";
        public static String SEND_CHAT_ACTION = "sendChatAction";
    }
    Set<UserMessageListener> userMessageListeners = new HashSet<>();

    Provider provider;

    String apiKey;

    Parser parser;

    int lastSeenUpdateId = 0;

    public Telegram(String apiKey) {
        this.apiKey = apiKey;
        this.parser = new Parser(this);

        this.provider = new PullProvider(1000, this);
        this.provider.setApiKey(apiKey);
        this.provider.start();
        this.ignoreMessageBefore = System.currentTimeMillis() / 1000;
    }




    public void addUserMessageListeners(UserMessageListener userMessageListener) {
        userMessageListeners.add(userMessageListener);
    }



    public void acceptUpdate(Update update) {
        if (update.message.date < ignoreMessageBefore) {
            return;
        }

        if (update.id > lastSeenUpdateId) {
            lastSeenUpdateId = update.id;
            if(update.message.chat instanceof User) {
                userMessageListeners.forEach(listener -> listener.userMessegeReceived(update.message));
            }
        }
    }

    public Object getUpdate() {
        return parser.parse(apiRequest(Endpoints.UPDATE, new HashMap<>()));
    }

    public void sendChatAction(int chatId, ChatAction action) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("chat_id", String.valueOf(chatId));
        arguments.put("action", String.valueOf(action));
        apiRequest(Endpoints.SEND_CHAT_ACTION, arguments);
    }

    public Message sendMessage(int chatId, String text) {
        return sendMessage(chatId, text, Optional.<Boolean>empty(), Optional.<Integer>empty());
    }

    /**
     *
     * @param chatId chat id to send to
     * @param text text t send
     * @param disableWebPageView if web page view is dissabled
     * @param replyToMessageId if this is a resopnd set to resondId
     * @return the messag send.
     */
    public Message sendMessage(int chatId, String text, Optional<Boolean> disableWebPageView, Optional<Integer> replyToMessageId) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("chat_id", String.valueOf(chatId));
        arguments.put("text", text);
        arguments.put("disable_web_page_view", String.valueOf(disableWebPageView.orElse(false)));
        if(replyToMessageId.isPresent()) {
            arguments.put("reply_to_message_id", String.valueOf(replyToMessageId.get()));
        }

        return (Message) ((Response) parser.parse(apiRequest(Endpoints.MESSAGE_SEND, arguments))).result[0];
    }

    private String apiRequest(String requestPoint, Map<String, String> arguments) {
        CloseableHttpClient httpClient = HttpClients.createDefault();


        List<NameValuePair>  formparams = arguments.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost request = new HttpPost(Telegram.API_URL + apiKey + "/" + requestPoint);

        request.setEntity(entity);

        try(CloseableHttpResponse response = httpClient.execute(request)) {

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            return rd.lines().collect(Collectors.joining());

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
