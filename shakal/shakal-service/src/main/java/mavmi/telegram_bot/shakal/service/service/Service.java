package mavmi.telegram_bot.shakal.service.service;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.utils.cache.ServiceCache;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.DiceJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.UserJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.UserMessageJson;
import mavmi.telegram_bot.common.utils.service.AbsService;
import mavmi.telegram_bot.common.utils.service.IMenu;
import mavmi.telegram_bot.shakal.service.constants.DicePhrases;
import mavmi.telegram_bot.shakal.service.constants.Goose;
import mavmi.telegram_bot.shakal.service.constants.Phrases;
import mavmi.telegram_bot.shakal.service.constants.Requests;
import mavmi.telegram_bot.shakal.service.http.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

@Slf4j
@Component
public class Service extends AbsService<UserCache> {

    private final HttpClient httpClient;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public Service(
            HttpClient httpClient,
            UserRepository userRepository,
            RequestRepository requestRepository,
            ServiceCache<UserCache> serviceCache
    ) {
        super(serviceCache);
        this.httpClient = httpClient;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public void handleRequest(BotRequestJson botRequestJson) {
        updateDatabase(botRequestJson);

        UserJson userJson = botRequestJson.getUserJson();
        long chatId = botRequestJson.getChatId();
        String username = null;
        String firstName = null;
        String lastName = null;
        String msg = null;

        if (userJson != null) {
            username = botRequestJson.getUserJson().getUsername();
            firstName = botRequestJson.getUserJson().getFirstName();
            lastName = botRequestJson.getUserJson().getLastName();
            msg = botRequestJson.getUserMessageJson().getTextMessage();
        }

        UserCache userCache = getUserCache(chatId, username, firstName, lastName);
        log.info("Got request. id: {}; username: {}; first name: {}; last name: {}; message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );
        IMenu userMenu = userCache.getMenu();

        if (userMenu == Menu.MAIN_MENU) {
            if (msg == null) {
                return;
            }

            switch (msg) {
                case (Requests.START_REQ) -> greetings(chatId);
                case (Requests.APOLOCHEESE_REQ) -> apolocheese_askForName(userCache);
                case (Requests.GOOSE_REQ) -> goose(userCache);
                case (Requests.ANEK_REQ) -> anek(userCache);
                case (Requests.MEME_REQ) -> meme(userCache);
                case (Requests.DICE_REQ) -> dice_init(userCache);
                case (Requests.HOROSCOPE_REQ) ->  horoscope_askForTitle(userCache);
                default -> error(userCache);
            }
        } else if (userMenu == Menu.APOLOCHEESE) {
            apolocheese_process(userCache, msg);
        } else if (userMenu == Menu.DICE) {
            dice_play(userCache, msg, botRequestJson.getDiceJson());
        } else if (userMenu == Menu.HOROSCOPE) {
            horoscope_process(userCache, msg);
        }
    }

    private void greetings(long chatId) {
        httpClient.sendText(chatId, Phrases.GREETINGS_MSG);
    }

    private void apolocheese_askForName(UserCache user) {
        user.setMenu(Menu.APOLOCHEESE);
        httpClient.sendText(user.getUserId(), Phrases.APOLOCHEESE_MSG);
    }

    private void apolocheese_process(UserCache user, String msg) {
        httpClient.sendText(user.getUserId(), generateApolocheese(msg));
        user.setMenu(Menu.MAIN_MENU);
    }

    private void goose(UserCache user) {
        httpClient.sendText(user.getUserId(), generateGoose());
    }

    private void anek(UserCache user) {
        httpClient.sendText(user.getUserId(), generateAnek());
    }

    private void meme(UserCache user) {
        PendingRequest request = Memes4J.getRandomMeme();

        try {
            httpClient.sendText(user.getUserId(), request.complete().getImage());
        } catch (Exception e) {
            httpClient.sendText(user.getUserId(), Phrases.EXCEPTION_MSG);
            e.printStackTrace(System.out);
        }
    }

    private void dice_init(UserCache user) {
        user.setMenu(Menu.DICE);
        httpClient.sendDice(user.getUserId(), Phrases.DICE_START, generateDiceArray());
    }

    private void dice_play(UserCache user, String msg, DiceJson diceJson) {
        if (diceJson != null) {
            if (diceJson.getBotDiceValue() != null) {
                user.setBotDice(diceJson.getBotDiceValue());
            } else {
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.out);
                    }

                    user.setUserDice(diceJson.getUserDiceValue());
                    String responseString;
                    if (user.getUserDice() > user.getBotDice()) {
                        responseString = DicePhrases.getRandomWinPhrase();
                    } else if (user.getUserDice() < user.getBotDice()) {
                        responseString = DicePhrases.getRandomLosePhrase();
                    } else {
                        responseString = DicePhrases.getRandomDrawPhrase();
                    }

                    httpClient.sendDice(user.getUserId(), responseString, generateDiceArray());
                }).start();
            }
        } else if (msg.equals(Phrases.DICE_QUIT_MSG)) {
            httpClient.sendText(user.getUserId(), Phrases.DICE_OK_MSG);
            user.setMenu(Menu.MAIN_MENU);
        } else {
            httpClient.sendDice(user.getUserId(), Phrases.DICE_ERROR_MSG, generateDiceArray());
        }
    }

    private void horoscope_askForTitle(UserCache user) {
        user.setMenu(Menu.HOROSCOPE);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.HOROSCOPE_QUES_MSG,
                generateHoroscopeArray()
        );
    }

    private void horoscope_process(UserCache user, String msg) {
        String sign = Phrases.HOROSCOPE_SIGNS.get(msg);
        if (sign == null) {
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.HOROSCOPE_ERROR_MSG,
                    generateHoroscopeArray()
            );
        } else {
            httpClient.sendText(user.getUserId(), generateHoroscope(sign));
            user.setMenu(Menu.MAIN_MENU);
        }
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

    private String generateGoose() {
        return Goose.getRandomGoose();
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
            e.printStackTrace(System.out);
            return Phrases.EXCEPTION_MSG;
        }
    }

    private String generateHoroscope(String sign) {
        try {
            Document document = Jsoup
                    .connect("https://horo.mail.ru/prediction/" + sign + "/today/")
                    .get();
            StringBuilder builder = new StringBuilder();

            int i = 0;
            for (Element element : document.getElementsByTag("p")) {
                if (i++ == 2) {
                    break;
                }
                if (!builder.isEmpty()) {
                    builder.append("\n").append("\n");
                }
                builder.append(element.text());
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return Phrases.EXCEPTION_MSG;
        }
    }

    private void error(UserCache user) {
        httpClient.sendText(user.getUserId(), Phrases.INVALID_COMMAND_MSG);
    }

    private UserCache getUserCache(Long chatId, String username, String firstName, String lastName) {
        UserCache user = serviceCache.getUser(chatId);

        if (user == null) {
            user = new UserCache(chatId, Menu.MAIN_MENU, username, firstName, lastName, true);
            serviceCache.putUser(user);
        }

        return user;
    }

    private void updateDatabase(BotRequestJson jsonDto) {
        UserJson userJson = jsonDto.getUserJson();
        UserMessageJson userMessageJson = jsonDto.getUserMessageJson();

        if (userJson != null) {
            userRepository.add(new UserModel(
                    userJson.getId(),
                    jsonDto.getChatId(),
                    userJson.getUsername(),
                    userJson.getFirstName(),
                    userJson.getLastName()
            ));
        }

        if (userMessageJson != null) {
            requestRepository.add(new RequestModel(
                    userJson.getId(),
                    userMessageJson.getTextMessage(),
                    new Date(userMessageJson.getDate().getTime() * 1000L),
                    new Time(userMessageJson.getDate().getTime() * 1000L)
            ));
        }
    }

    private String[] generateHoroscopeArray() {
        int i = 0;
        String[] arr = new String[12];

        for (Map.Entry<String, String> entry : Phrases.HOROSCOPE_SIGNS.entrySet()) {
            arr[i++] = entry.getKey();
        }

        return arr;
    }

    private String[] generateDiceArray() {
        return new String[]{
                Phrases.DICE_THROW_MSG,
                Phrases.DICE_QUIT_MSG
        };
    }
}
