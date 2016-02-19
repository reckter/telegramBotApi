package me.reckter.telegram;

/**
 * @author hannes
 */
public interface Provider {

    void setApiKey(String key);

    void start();

    void end();
}
