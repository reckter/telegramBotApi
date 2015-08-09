package reckter.telegram;

import reckter.json.Parser;
import reckter.telegram.model.Message;
import reckter.telegram.model.Response;
import reckter.telegram.model.Update;
import reckter.telegram.model.User;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author hannes
 */
public class PullProvider implements Provider {

    String apiKey;
    int sleep;
    boolean isAlive = false;

    Telegram telegram;
    Parser parser;

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
        if(apiKey == null) {
            throw new InvalidStateException("Don't have a apikey!");
        }
        isAlive = true;
        pullThread.start();
    }

    @Override
    public void end() {
        isAlive = false;
        pullThread.interrupt();
    }

    private  Thread pullThread = new Thread(() -> {

        URL url = null;
        try {
            url = new URL(Telegram.API_URL + apiKey + "/" +  Telegram.Endpoints.UPDATE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        while (isAlive) {
            try {
                URLConnection con = url.openConnection();
                String response = (new BufferedReader(new InputStreamReader(con.getInputStream()))).lines().reduce(String::concat).get();

                Object objects = parser.parse(response);
                Response res = ((Response) objects);

                for (Object o : res.result) {
                    telegram.acceptUpdate((Update) o);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("pullThread interrupted.");
            }
        }
    });
}
