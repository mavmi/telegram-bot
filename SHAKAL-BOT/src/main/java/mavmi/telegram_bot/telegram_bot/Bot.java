package mavmi.telegram_bot.telegram_bot;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.constants.DicePhrases;
import mavmi.telegram_bot.constants.Goose;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.*;

import static mavmi.telegram_bot.constants.Levels.*;
import static mavmi.telegram_bot.constants.Phrases.*;
import static mavmi.telegram_bot.constants.Requests.*;

public class Bot {
    private final static ReplyKeyboardMarkup diceKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);
    static{
        diceKeyboard.addRow(DICE_THROW_MSG);
        diceKeyboard.addRow(DICE_QUIT_MSG);
    }
    private final static ReplyKeyboardMarkup horoscopeKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);
    static {
        for (Map.Entry<String, String> entry : HOROSCOPE_SIGNS.entrySet()){
            horoscopeKeyboard.addRow(entry.getKey());
        }
    }

    private String token;
    private Logger logger;
    private TelegramBot telegramBot;

    private final Map<Long, User> users;

    public Bot(){
        this.users = new HashMap<>();
    }

    public Bot setToken(String token){
        this.token = token;
        return this;
    }
    public Bot setLogger(Logger logger){
        this.logger = logger;
        return this;
    }

    public void run(){
        telegramBot = new TelegramBot(token);
        logger.log("SHAKAL-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                logger.log(generateLogLine(update));
                logger.log(update.message());

                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final User user = processUsername(update.message().from());

                if (user.getState() == MAIN_LEVEL) {
                    if (inputText == null) continue;
                    switch (inputText) {
                        case (START_REQ) -> greetings(chatId);
                        case (APOLOCHEESE_REQ) -> apolocheese(chatId, inputText, user);
                        case (GOOSE_REQ) -> goose(chatId);
                        case (ANEK_REQ) -> anek(chatId);
                        case (MEME_REQ) -> meme(chatId);
                        case (DICE_REQ) -> dice(chatId, user, update.message());
                        case (HOROSCOPE_REQ) -> horoscope(chatId, user, update.message());
                        default -> sendMsg(chatId, generateErrorMsg());
                    }
                } else if (user.getState() == APOLOCHEESE_LEVEL){
                    apolocheese(chatId, inputText, user);
                } else if (user.getState() == DICE_LEVEL){
                    dice(chatId, user, update.message());
                } else if (user.getState() == HOROSCOPE_LEVEL){
                    horoscope(chatId, user, update.message());
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void sendMsg(long chatId, String msg){
        telegramBot.execute(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));
    }

    private void greetings(long chatId){
        sendMsg(chatId, GREETINGS_MSG);
    }
    private void apolocheese(long chatId, String inputText, User user){
        if (user.getState() == MAIN_LEVEL){
            user.setState(APOLOCHEESE_LEVEL);
            sendMsg(chatId, APOLOCHEESE_MSG);
        } else if (user.getState() == APOLOCHEESE_LEVEL){
            sendMsg(chatId, generateApolocheese(inputText));
            user.setState(MAIN_LEVEL);
        }
    }
    private void goose(long chatId){
        sendMsg(chatId, generateGoose());
    }
    private void anek(long chatId){
        telegramBot.execute(new SendMessage(chatId, generateAnek()));
    }
    private void meme(long chatId){
        PendingRequest request = Memes4J.getRandomMeme();
        try {
            sendMsg(chatId, request.complete().getImage());
        } catch (Exception e){
            sendMsg(chatId, EXCEPTION_MSG);
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
        } else if (message.text().equals(DICE_QUIT_MSG)) {
            telegramBot.execute(new SendMessage(chatId, DICE_OK_MSG));
            user.setState(MAIN_LEVEL);
        } else {
            telegramBot.execute(new SendMessage(chatId, DICE_ERROR_MSG).replyMarkup(diceKeyboard));
        }
    }
    private void horoscope(long chatId, User user, Message message){
        if (user.getState() == MAIN_LEVEL){
            user.setState(HOROSCOPE_LEVEL);
            telegramBot.execute(new SendMessage(chatId, HOROSCOPE_QUES_MSG).replyMarkup(horoscopeKeyboard));
        } else {
            String sign = HOROSCOPE_SIGNS.get(message.text());
            if (sign == null){
                telegramBot.execute(new SendMessage(chatId, HOROSCOPE_ERROR_MSG).replyMarkup(horoscopeKeyboard));
            } else {
                telegramBot.execute(new SendMessage(chatId, generateHoroscope(sign)));
                user.setState(MAIN_LEVEL);
            }
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
    private String generateAnek(){
        try {
            final String begin = "<div class=\"text\">";
            final String end = "</div>";

            URL url = new URL("https://www.anekdot.ru/random/anekdot/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int state = 0;
            String line;
            StringBuilder builder = new StringBuilder();
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
            return res.substring(0, res.indexOf(end)).replaceAll("<br>", "\n");
        } catch (Exception e) {
            logger.log(e.getMessage());
            return EXCEPTION_MSG;
        }
    }
    private String generateHoroscope(String sign){
        try{
            final String begin = "<p>";
            final String end = "</p>";

            URL url = new URL("https://horo.mail.ru/prediction/" + sign + "/today/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int state = 0;
            String line;
            StringBuilder tmpBuilder = new StringBuilder();
            StringBuilder resBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null){
                if (state == 0){
                    int index = line.indexOf(begin);
                    if (index == -1) continue;
                    tmpBuilder.append(line);
                    state = 1;
                    continue;
                }
                if (state == 1){
                    int index = tmpBuilder.indexOf(end);
                    if (index == -1) tmpBuilder.append(line);
                    else {
                        if (resBuilder.length() > 0) resBuilder.append("\n\n");
                        resBuilder.append(
                                tmpBuilder.substring(tmpBuilder.indexOf(begin) + begin.length(), tmpBuilder.indexOf(end)).
                                replaceAll("&nbsp;", " ")
                        );
                        tmpBuilder.setLength(0);
                        state = 0;
                    }
                }
            }

            return resBuilder.toString();
        } catch (Exception e){
            logger.log(e.getMessage());
            return EXCEPTION_MSG;
        }
    }
    private String generateErrorMsg(){
        return INVALID_COMMAND_MSG;
    }

    private String generateLogLine(Update update){
        return new StringBuilder()
                .append("USER_ID: [")
                .append(update.message().from().id())
                .append("], ")
                .append("USERNAME: [")
                .append(update.message().from().username())
                .append("], ")
                .append("FIRST_NAME: [")
                .append(update.message().from().firstName())
                .append("], ")
                .append("LAST_NAME: [")
                .append(update.message().from().lastName())
                .append("], ")
                .append("MESSAGE: [")
                .append(update.message().text())
                .append("]")
                .toString();
    }
    private User processUsername(com.pengrad.telegrambot.model.User telegramUser){
        User user = users.get(telegramUser.id());
        if (user == null){
            user = new User()
                    .setId(telegramUser.id())
                    .setUsername(telegramUser.username())
                    .setFirstName(telegramUser.firstName())
                    .setLastName(telegramUser.lastName());
            users.put(user.getId(), user);
        }
        return user;
    }

}
