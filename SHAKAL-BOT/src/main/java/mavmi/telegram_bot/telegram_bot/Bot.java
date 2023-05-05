package mavmi.telegram_bot.telegram_bot;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.constants.Goose;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
        logger.log("SHAKAL-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                logger.log(generateLogLine(update));

                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final String username = update.message().from().username();

                AtomicInteger userState = availableUsers.get(username);
                if (userState == null) continue;

                if (userState.get() == MAIN_LEVEL) {
                    switch (inputText) {
                        case (START_REQ) -> greetings(chatId);
                        case (APOLOCHEESE_REQ) -> apolocheese(chatId, inputText, userState);
                        case (GOOSE_REQ) -> goose(chatId);
                        case (ANEK_REQ) -> anek(chatId);
                        case (MEME_REQ) -> meme(chatId);
                        default -> sendMsg(chatId, generateErrorMsg());
                    }
                } else if (userState.get() == APOLOCHEESE_LEVEL){
                    apolocheese(chatId, inputText, userState);
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
    private void apolocheese(long chatId, String inputText, AtomicInteger userState){
        if (userState.get() == MAIN_LEVEL){
            userState.set(APOLOCHEESE_LEVEL);
            sendMsg(chatId, "Для кого оформляем, брат?");
        } else if (userState.get() == APOLOCHEESE_LEVEL){
            sendMsg(chatId, generateApolocheese(inputText));
            userState.set(MAIN_LEVEL);
        }
    }
    private void goose(long chatId){
        sendMsg(chatId, generateGoose());
    }
    private void anek(long chatId){
        try {
            final String begin = "<div class=\"text\">";
            final String end = "</div>";
            int state = 0;

            URL url = new URL("https://www.anekdot.ru/random/anekdot/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            StringBuilder builder = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null){
                if (state == 0){
                    int index = line.indexOf(begin);
                    if (index == -1) continue;
                    builder.append(line);
                    state = 1;
                    continue;
                }
                if (state == 1) {
                    int index = builder.indexOf(end);
                    if (index == -1) builder.append(line);
                    else break;
                }
            }

            String res = builder.substring(builder.indexOf(begin) + begin.length());
            sendMsg(chatId, res.substring(0, res.indexOf(end)).replaceAll("<br>", "\n"));
        } catch (Exception e) {
            sendMsg(chatId, "Сорян, братишка. Что-то пиздой пошло. Я хз. Попробуй по новой");
            logger.log(e.getMessage());
        }
    }
    private void meme(long chatId){
        PendingRequest request = Memes4J.getRandomMeme();
        try {
            sendMsg(chatId, request.complete().getImage());
        } catch (Exception e){
            sendMsg(chatId, "Что-то поломалось. Типа лол. Типа хз");
            logger.log(e.getMessage());
        }
    }

    private String generateGoose(){
        return Goose.getRandomGoose();
    }
    private String generateApolocheese(String username){
        final StringBuilder builder = new StringBuilder();
        builder.append("```\n")
                .append("java -jar \"/home/mavmi/apolocheese/apolocheese.jar\"")
                .append("\n\n")
                .append(new SimpleDateFormat("dd.MM.yyyy HH:mm:").format(GregorianCalendar.getInstance().getTime()))
                .append("```")
                .append("\n")
                .append("\"Я прошу прощения, ")
                .append(username)
                .append("! Солнышко! Я дико извиняюсь! Сможешь ли ты меня простить?.....\"")
                .append("\n")
                .append("\n")
                .append("```\n")
                .append("@https://github.com/mavmi\n")
                .append("@All rights reserved!\n")
                .append("@Do not distribute!\n")
                .append("```");
        return builder.toString();
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
