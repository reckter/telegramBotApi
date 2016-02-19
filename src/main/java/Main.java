import me.reckter.telegram.Telegram;
import me.reckter.telegram.listener.OnLocation;
import me.reckter.telegram.listener.OnMessage;
import me.reckter.telegram.model.Message;
import me.reckter.telegram.requests.ChatAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by hannes on 12.02.16.
 */
@SpringBootApplication
@Component
@Configuration
@ComponentScan(basePackages = "me.reckter")
public class Main  {


    public static int ADMIN_ACC = 71580123;
    public static String BOT_KEY = "162207917:AAGMMn8Jcu80nX6ixYYe5LnSP7lKyFPiHQs";
    public static String ERROR_BOT_KEY = "162555530:AAHaBtkJd2XdNvyRpDa8CzvnJFe-gayf0zw";

    @Autowired
    Telegram telegram;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    public void run(String... args) throws Exception {
        telegram.addListener(this);
    }

    @OnMessage
    public void echo(Message message, Map<String, String> groups) {
        StringBuilder text = new StringBuilder();
        for(int i = 0; i < 100; i++) {
            text.append(message.getText()).append("\n");
        }
        telegram.sendMessage(message.chat.getId(), text.toString());
    }

    @OnLocation
    public void onLocation(Message message) {
        message.chat.sendAction(ChatAction.find_locatio);
        try {
            Thread.sleep(5000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        telegram.sendLocation(message.chat.id, message.location);
    }


    @Bean
    public Telegram telegram() {
        return new Telegram(BOT_KEY, ADMIN_ACC, ERROR_BOT_KEY);
    }
}
