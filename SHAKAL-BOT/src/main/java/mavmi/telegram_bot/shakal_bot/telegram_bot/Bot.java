package mavmi.telegram_bot.shakal_bot.telegram_bot;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import jakarta.annotation.PostConstruct;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.shakal_bot.constants.DicePhrases;
import mavmi.telegram_bot.shakal_bot.constants.Goose;
import mavmi.telegram_bot.shakal_bot.constants.Levels;
import mavmi.telegram_bot.shakal_bot.constants.Phrases;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class Bot extends AbsTelegramBot {
    private final static ReplyKeyboardMarkup diceKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);

    static {
        diceKeyboard.addRow(Phrases.DICE_THROW_MSG);
        diceKeyboard.addRow(Phrases.DICE_QUIT_MSG);
    }

    private final static ReplyKeyboardMarkup horoscopeKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);

    static {
        for (Map.Entry<String, String> entry : Phrases.HOROSCOPE_SIGNS.entrySet()) {
            horoscopeKeyboard.addRow(entry.getKey());
        }
    }

    private final TelegramBot telegramBot;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private final Map<Long, User> users = new HashMap<>();

    public Bot(
            @Value("${bot.token}") String telegramBotToken,
            UserRepository userRepository,
            RequestRepository requestRepository,
            Logger logger
    ) {
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @PostConstruct
    public void run() {
        logger.log("SHAKAL-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                logEvent(update.message());
                updateTables(update.message());
                new ProcessRequest(this, update).start();
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    synchronized <T extends BaseRequest<T, R>, R extends BaseResponse> R sendMsg(BaseRequest<T, R> baseRequest) {
        return telegramBot.execute(baseRequest);
    }

    void greetings(long chatId) {
        sendMsg(new SendMessage(chatId, Phrases.GREETINGS_MSG));
    }

    void apolocheese(long chatId, String inputText, User user) {
        if (user.getState() == Levels.MAIN_LEVEL) {
            user.setState(Levels.APOLOCHEESE_LEVEL);
            sendMsg(new SendMessage(chatId, Phrases.APOLOCHEESE_MSG));
        } else if (user.getState() == Levels.APOLOCHEESE_LEVEL) {
            sendMsg(new SendMessage(chatId, generateApolocheese(inputText)).parseMode(ParseMode.Markdown));
            user.setState(Levels.MAIN_LEVEL);
        }
    }

    void goose(long chatId) {
        sendMsg(new SendMessage(chatId, generateGoose()));
    }

    void anek(long chatId) {
        sendMsg(new SendMessage(chatId, generateAnek()));
    }

    void meme(long chatId) {
        PendingRequest request = Memes4J.getRandomMeme();
        try {
            sendMsg(new SendMessage(chatId, request.complete().getImage()));
        } catch (Exception e) {
            sendMsg(new SendMessage(chatId, Phrases.EXCEPTION_MSG));
            logger.log(e.getMessage());
        }
    }

    void dice(long chatId, User user, Message message) {
        if (user.getState() == Levels.MAIN_LEVEL) {
            user.setState(Levels.DICE_LEVEL);
            user.setBotDice(sendMsg(new SendDice(chatId).replyMarkup(diceKeyboard)).message().dice().value());
        } else if (message.dice() != null) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.log(e.getMessage());
            }
            user.setUserDice(message.dice().value());
            if (user.getUserDice() > user.getBotDice())
                sendMsg(new SendMessage(chatId, DicePhrases.getRandomWinPhrase()));
            else if (user.getUserDice() < user.getBotDice())
                sendMsg(new SendMessage(chatId, DicePhrases.getRandomLosePhrase()));
            user.setBotDice(sendMsg(new SendDice(chatId).replyMarkup(diceKeyboard)).message().dice().value());
        } else if (message.text().equals(Phrases.DICE_QUIT_MSG)) {
            sendMsg(new SendMessage(chatId, Phrases.DICE_OK_MSG));
            user.setState(Levels.MAIN_LEVEL);
        } else {
            sendMsg(new SendMessage(chatId, Phrases.DICE_ERROR_MSG).replyMarkup(diceKeyboard));
        }
    }

    void horoscope(long chatId, User user, Message message) {
        if (user.getState() == Levels.MAIN_LEVEL) {
            user.setState(Levels.HOROSCOPE_LEVEL);
            sendMsg(new SendMessage(chatId, Phrases.HOROSCOPE_QUES_MSG).replyMarkup(horoscopeKeyboard));
        } else {
            String sign = Phrases.HOROSCOPE_SIGNS.get(message.text());
            if (sign == null) {
                sendMsg(new SendMessage(chatId, Phrases.HOROSCOPE_ERROR_MSG).replyMarkup(horoscopeKeyboard));
            } else {
                sendMsg(new SendMessage(chatId, generateHoroscope(sign)));
                user.setState(Levels.MAIN_LEVEL);
            }
        }
    }

    String generateErrorMsg() {
        return Phrases.INVALID_COMMAND_MSG;
    }

    private String generateGoose() {
        return Goose.getRandomGoose();
    }

    private String generateApolocheese(String username) {
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

    private String generateAnek() {
        try {
            Document document = Jsoup.connect("https://www.anekdot.ru/random/anekdot/").get();
            for (Element element : document.getElementsByTag("div")) {
                if (element.className().equals("text")) {
                    return element.text();
                }
            }
            throw new IOException();
        } catch (IOException e) {
            logger.err(e.getMessage());
            return Phrases.EXCEPTION_MSG;
        }
    }

    private String generateHoroscope(String sign) {
        try {
            Document document = Jsoup.connect("https://horo.mail.ru/prediction/" + sign + "/today/").get();
            StringBuilder builder = new StringBuilder();
            for (Element element : document.getElementsByTag("p")) {
                if (builder.length() != 0) builder.append("\n").append("\n");
                builder.append(element.text());
            }
            return builder.toString();
        } catch (IOException e) {
            logger.err(e.getMessage());
            return Phrases.EXCEPTION_MSG;
        }
    }

    synchronized User processUsername(com.pengrad.telegrambot.model.Message telegramMessage) {
        User user = users.get(telegramMessage.from().id());
        if (user == null) {
            user = new User()
                    .setId(telegramMessage.from().id())
                    .setChatId(telegramMessage.chat().id())
                    .setUsername(telegramMessage.from().username())
                    .setFirstName(telegramMessage.from().firstName())
                    .setLastName(telegramMessage.from().lastName());
            users.put(user.getId(), user);
        }
        return user;
    }

    private void updateTables(Message message) {
        com.pengrad.telegrambot.model.User user = message.from();

        userRepository.add(new UserModel(
                user.id(),
                message.chat().id(),
                user.username(),
                user.firstName(),
                user.lastName()
        ));

        requestRepository.add(new RequestModel(
                user.id(),
                message.text(),
                new Date((long) message.date() * 1000L),
                new Time((long) message.date() * 1000L)
        ));
    }
}
