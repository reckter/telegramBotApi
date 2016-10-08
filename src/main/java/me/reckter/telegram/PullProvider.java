package me.reckter.telegram;

import me.reckter.telegram.model.update.Update;

import java.util.List;


/**
 * @author hannes
 */
public class PullProvider implements Provider {

    String apiKey;
    int sleep;
    boolean isAlive = false;

    Telegram telegram;

    long lastSeenUpdateId = 0;

    public PullProvider(int sleep, Telegram telegram) {
        this.telegram = telegram;
        this.sleep = sleep;
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
             List<Update> updates = telegram.getUpdates(lastSeenUpdateId + 1, 100, sleep * 100);

            for(Update update: updates) {
                if (lastSeenUpdateId < update.getId()) {
                    lastSeenUpdateId = update.getId();
                }
                telegram.acceptUpdate(update);
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
