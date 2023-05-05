package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

import static mavmi.telegram_bot.constants.Levels.*;
import static mavmi.telegram_bot.constants.Requests.*;

public class Bot {
    private Logger logger;

    private final Map<String, AtomicInteger> availableUsers = new HashMap<>();
    private final TelegramBot telegramBot;

    public Bot(String token, String[] availableUsers, Logger logger){
        for (String username : availableUsers) this.availableUsers.put(username, new AtomicInteger(MAIN_LEVEL));
        telegramBot = new TelegramBot(token);
        this.logger = logger;
    }

    public void run(){
        logger.log("WATER-STUFF-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                logger.log(generateLogLine(update));

                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final String username = update.message().from().username();

                AtomicInteger userState = availableUsers.get(username);
                if (userState == null) continue;

                if (userState.get() == MAIN_LEVEL) {
                    switch (inputText){
                        case (START_REQ) -> greetings(chatId);
                        case (GET_INFO_REQ) -> {}
                        case (WATER_REQ) -> {}
                        case (FERTILIZE_REQ) -> {}
                        default -> sendMsg(chatId, generateErrorMsg());
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void sendMsg(long chatId, String msg){
        telegramBot.execute(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));
    }

    private void greetings(long chatId){
        sendMsg(chatId, "Здравстуйте.");
    }

    private String generateErrorMsg(){
        return  "не вдупляю";
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
