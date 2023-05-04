package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Bot {
    private static final int MAIN = 0;
    private static final int APOLOCHEESE = 1;

    private static final String GOOSE_REQ = "/goose";
    private static final String APOLOCHEESE_REQ = "/apolocheese";
    private static final String GET_INFO_REQ = "/info";
    private static final String WATER_REQ = "/water";
    private static final String FERTILIZE_REQ = "/fertilize";

    private final Map<String, AtomicInteger> availableUsers = new HashMap<>();
    private final TelegramBot telegramBot;

    public Bot(String token, String[] availableUsers){
        for (String username : availableUsers) this.availableUsers.put(username, new AtomicInteger(MAIN));
        telegramBot = new TelegramBot(token);
    }

    public void run(){
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final String username = update.message().from().username();

                AtomicInteger userState = availableUsers.get(username);
                if (userState == null) continue;

                if (userState.get() == MAIN) {
                    if (inputText.equals(APOLOCHEESE_REQ)) {
                        apolocheese(chatId, inputText, userState);
                    } else if (inputText.equals(GOOSE_REQ)) {
                        goose(chatId);
                    } else if (inputText.equals(GET_INFO_REQ)) {

                    } else if (inputText.equals(WATER_REQ)) {

                    } else if (inputText.equals(FERTILIZE_REQ)) {

                    } else {
                        sendMsg(chatId, generateErrorMsg());
                    }
                } else if (userState.get() == APOLOCHEESE){
                    apolocheese(chatId, inputText, userState);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void sendMsg(long chatId, String msg){
        telegramBot.execute(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));
    }

    private void apolocheese(long chatId, String inputText, AtomicInteger userState){
        if (userState.get() == MAIN){
            userState.set(APOLOCHEESE);
            sendMsg(chatId, "Для кого оформляем, брат?");
        } else if (userState.get() == APOLOCHEESE){
            sendMsg(chatId, generateApolocheese(inputText));
            userState.set(MAIN);
        }
    }
    private void goose(long chatId){
        sendMsg(chatId, generateGoose());
    }

    private String generateGoose(){
        final StringBuilder builder = new StringBuilder();
        builder.append("```\n")
                .append("░░░░░░░░░░░░░░░░░░░░\n")
                .append("░░░░░ЗАПУСКАЕМ░░░░░░░\n")
                .append("░ГУСЯ░▄▀▀▀▄░РАБОТЯГИ░░\n")
                .append("▄███▀░◐░░░▌░░░░░░░░░\n")
                .append("░░░░▌░░░░░▐░░░░░░░░░\n")
                .append("░░░░▐░░░░░▐░░░░░░░░░\n")
                .append("░░░░▌░░░░░▐▄▄░░░░░░░\n")
                .append("░░░░▌░░░░▄▀▒▒▀▀▀▀▄\n")
                .append("░░░▐░░░░▐▒▒▒▒▒▒▒▒▀▀▄\n")
                .append("░░░▐░░░░▐▄▒▒▒▒▒▒▒▒▒▒▀▄\n")
                .append("░░░░▀▄░░░░▀▄▒▒▒▒▒▒▒▒▒▒▀▄\n")
                .append("░░░░░░▀▄▄▄▄▄█▄▄▄▄▄▄▄▄▄▄▄▀▄\n")
                .append("░░░░░░░░░░░▌▌░▌▌░░░░░\n")
                .append("░░░░░░░░░░░▌▌░▌▌░░░░░\n")
                .append("░░░░░░░░░▄▄▌▌▄▌▌░░░░░\n")
                .append("```");

        return builder.toString();
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

}
