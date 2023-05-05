package mavmi.telegram_bot.telegram_bot;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Dice;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import mavmi.telegram_bot.constants.DicePhrases;
import mavmi.telegram_bot.constants.Goose;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static mavmi.telegram_bot.constants.Levels.*;
import static mavmi.telegram_bot.constants.Requests.*;

public class Bot {
    private final Keyboard diceKeyboard = new ReplyKeyboardMarkup(
            new KeyboardButton("\uD83C\uDFB2"),
            new KeyboardButton("дать заднюю (выйти)")
    ).oneTimeKeyboard(true).resizeKeyboard(true);

    private Logger logger;

    private final Map<Long, long[]> userIdToMillis = new HashMap<>();
    private final Map<String, User> availableUsers = new HashMap<>();
    private final TelegramBot telegramBot;

    public Bot(String token, String[] availableUsers, Logger logger){
        for (String username : availableUsers) {
            this.availableUsers.put(username, new User().setUsername(username));
        }
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

                User user = availableUsers.get(username);
                if (user == null) continue;

                if (user.getState() == MAIN_LEVEL) {
                    switch (inputText) {
                        case (START_REQ) -> greetings(chatId);
                        case (APOLOCHEESE_REQ) -> apolocheese(chatId, inputText, user);
                        case (GOOSE_REQ) -> goose(chatId);
                        case (ANEK_REQ) -> anek(chatId);
                        case (MEME_REQ) -> meme(chatId);
                        case (DICE_REQ) -> dice(chatId, user, update.message());
                        default -> sendMsg(chatId, generateErrorMsg());
                    }
                } else if (user.getState() == APOLOCHEESE_LEVEL){
                    apolocheese(chatId, inputText, user);
                } else if (user.getState() == DICE_LEVEL){
                    dice(chatId, user, update.message());
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
    private void apolocheese(long chatId, String inputText, User user){
        if (user.getState() == MAIN_LEVEL){
            user.setState(APOLOCHEESE_LEVEL);
            sendMsg(chatId, "Для кого оформляем, брат?");
        } else if (user.getState() == APOLOCHEESE_LEVEL){
            sendMsg(chatId, generateApolocheese(inputText));
            user.setState(MAIN_LEVEL);
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
    private void dice(long chatId, User user, Message message){
        if (user.getState() == MAIN_LEVEL){
            user.setState(DICE_LEVEL);
            user.setBotDice(telegramBot.execute(new SendDice(chatId).replyMarkup(diceKeyboard)).message().dice().value());
        } else if (message.dice() != null) {
            user.setUserDice(message.dice().value());
            if (user.getUserDice() > user.getBotDice()) telegramBot.execute(new SendMessage(chatId, DicePhrases.getRandomWinPhrase()));
            else if (user.getUserDice() < user.getBotDice()) telegramBot.execute(new SendMessage(chatId, DicePhrases.getRandomLosePhrase()));
            user.setBotDice(telegramBot.execute(new SendDice(chatId).replyMarkup(diceKeyboard)).message().dice().value());
        } else if (message.text().equals("дать заднюю (выйти)")) {
            telegramBot.execute(new SendMessage(chatId, "Ладно"));
            user.setState(MAIN_LEVEL);
        } else {
            telegramBot.execute(new SendMessage(chatId, "Каво?").replyMarkup(diceKeyboard));
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
