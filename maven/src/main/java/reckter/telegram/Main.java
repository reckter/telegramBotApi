package reckter.telegram;

/**
 * @author hannes
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Telegram telegram = new Telegram("bot111085299:AAFazOiH43gGtVtNV2HI8KsK6LmcfBIBfHw");

        telegram.parser.staticAddParsables();

        telegram.addUserMessageListeners(message -> {
                message.respond("'" + message.text + "' <- responded!");
        });

    }
}
