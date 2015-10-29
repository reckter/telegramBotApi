package me.reckter.telegram;

import me.reckter.json.Parser;
import me.reckter.telegram.model.Message;
import me.reckter.telegram.model.Response;
import me.reckter.telegram.model.Update;
import me.reckter.telegram.model.User;

/**
 * @author hannes
 */
public class PullProvider implements Provider {

    String apiKey;
    int sleep;
    boolean isAlive = false;

    Telegram telegram;
    Parser parser;

    int lastSeenUpdateId = 0;

    public PullProvider(int sleep, Telegram telegram) {
        this.telegram = telegram;
        parser = new Parser(telegram);
        this.sleep = sleep;
        parser.addParsable(Message.class);
        parser.addParsable(Response.class);
        parser.addParsable(Update.class);
        parser.addParsable(User.class);
    }

    @Override
    public void setApiKey(String key) {
        this.apiKey = key;
    }

    @Override
    public void start() {
        if (apiKey == null) {
            throw new java.lang.RuntimeException("Don't have a apikey!");
        }
        isAlive = true;
        System.out.println("starting pulling..");
        pullThread.start();
    }

    @Override
    public void end() {
        isAlive = false;
        pullThread.interrupt();
    }

    private Thread pullThread = new Thread(() -> {
        while (isAlive) {
            long time = System.currentTimeMillis();
            Object objects = telegram.getUpdate(lastSeenUpdateId + 1, 100, sleep * 10);
            Response res = ((Response) objects);

            for (Object o : res.result) {
                if (lastSeenUpdateId < ((Update) o).id) {
                    lastSeenUpdateId = ((Update) o).id;
                }
                telegram.acceptUpdate((Update) o);
            }

            long timeToSleep = sleep - (System.currentTimeMillis() - time);
            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException ignored) {
                }
            }
        }
    });
}
