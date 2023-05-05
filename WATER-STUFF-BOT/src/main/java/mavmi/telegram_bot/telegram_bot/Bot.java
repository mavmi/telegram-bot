package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class Bot {
    private Logger logger;

    private static final int MAIN = 0;

    private static final String GET_INFO_REQ = "/info";
    private static final String WATER_REQ = "/water";
    private static final String FERTILIZE_REQ = "/fertilize";


    private final Map<String, AtomicInteger> availableUsers = new HashMap<>();
    private final TelegramBot telegramBot;

    public Bot(String token, String[] availableUsers, Logger logger){
        for (String username : availableUsers) this.availableUsers.put(username, new AtomicInteger(MAIN));
        telegramBot = new TelegramBot(token);
        this.logger = logger;
    }

    public void run(){
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                logger.log(generateLogLine(update));

                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final String username = update.message().from().username();

                AtomicInteger userState = availableUsers.get(username);
                if (userState == null) continue;

                if (userState.get() == MAIN) {
                    if (inputText.equals(GET_INFO_REQ)) {

                    } else if (inputText.equals(WATER_REQ)) {

                    } else if (inputText.equals(FERTILIZE_REQ)) {

                    } else {
                        sendMsg(chatId, generateErrorMsg());
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void sendMsg(long chatId, String msg){
        telegramBot.execute(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));
    }

    private String generateErrorMsg(){
        return  "я не выкупаю...";
    }

    private String generateLogLine(Update update){
        return new StringBuilder()
                .append("USERNAME: [")
                .append(update.message().from().username())
                .append("], ")
                .append("FIRST NAME: [")
                .append(update.message().from().firstName())
                .append("], ")
                .append("LAST NAME: [")
                .append(update.message().from().lastName())
                .append("], ")
                .append("MESSAGE: [")
                .append(update.message().text())
                .append("]")
                .toString();
    }

}
