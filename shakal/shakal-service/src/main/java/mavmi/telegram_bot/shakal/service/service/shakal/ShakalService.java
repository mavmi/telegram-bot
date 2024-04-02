package mavmi.telegram_bot.shakal.service.service.shakal;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.tasks.SHAKAL_SERVICE_TASK;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.service.AbstractService;
import mavmi.telegram_bot.common.service.menu.IMenu;
import mavmi.telegram_bot.shakal.service.cache.UserDataCache;
import mavmi.telegram_bot.shakal.service.constants.DicePhrases;
import mavmi.telegram_bot.shakal.service.constants.Goose;
import mavmi.telegram_bot.shakal.service.constants.Phrases;
import mavmi.telegram_bot.shakal.service.constants.Requests;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.Menu;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

@Slf4j
@Component
public class ShakalService extends AbstractService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Autowired
    private UserSession userSession;

    public ShakalService(
            UserRepository userRepository,
            RequestRepository requestRepository
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public ShakalServiceRs handleRequest(ShakalServiceRq shakalServiceRq) {
        updateDatabase(shakalServiceRq);

        UserJson userJson = shakalServiceRq.getUserJson();
        String msg = null;

        if (userJson != null) {
            msg = shakalServiceRq.getMessageJson().getTextMessage();
        }

        UserDataCache userCache = userSession.getCache();
        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );
        IMenu userMenu = userCache.getMenu();

        if (userMenu == Menu.MAIN_MENU) {
            if (msg == null) {
                return error();
            }

            return switch (msg) {
                case (Requests.START_REQ) -> greetings();
                case (Requests.APOLOCHEESE_REQ) -> apolocheese_askForName();
                case (Requests.GOOSE_REQ) -> goose();
                case (Requests.ANEK_REQ) -> anek();
                case (Requests.MEME_REQ) -> meme();
                case (Requests.DICE_REQ) -> dice_init();
                case (Requests.HOROSCOPE_REQ) ->  horoscope_askForTitle();
                default -> error();
            };
        } else if (userMenu == Menu.APOLOCHEESE) {
            return apolocheese_process(msg);
        } else if (userMenu == Menu.DICE) {
            return dice_play(msg, shakalServiceRq.getDiceJson());
        } else if (userMenu == Menu.HOROSCOPE) {
            return horoscope_process(msg);
        } else {
            return error();
        }
    }

    @Override
    public AbstractUserDataCache initCache() {
        return new UserDataCache(userSession.getId(), Menu.MAIN_MENU);
    }

    private ShakalServiceRs greetings() {
        return createSendTextResponse(Phrases.GREETINGS_MSG);
    }

    private ShakalServiceRs apolocheese_askForName() {
        userSession.getCache().setMenu(Menu.APOLOCHEESE);
        return createSendTextResponse(Phrases.APOLOCHEESE_MSG);
    }

    private ShakalServiceRs apolocheese_process(String msg) {
        userSession.getCache().setMenu(Menu.MAIN_MENU);
        return createSendTextResponse(generateApolocheese(msg));
    }

    private ShakalServiceRs goose() {
        return createSendTextResponse(generateGoose());
    }

    private ShakalServiceRs anek() {
        return createSendTextResponse(generateAnek());
    }

    private ShakalServiceRs meme() {
        PendingRequest request = Memes4J.getRandomMeme();

        try {
            return createSendTextResponse(request.complete().getImage());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return createSendTextResponse(Phrases.EXCEPTION_MSG);
        }
    }

    private ShakalServiceRs dice_init() {
        userSession.getCache().setMenu(Menu.DICE);
        return createSendDiceResponse(Phrases.DICE_START, generateDiceArray());
    }

    private ShakalServiceRs dice_play(String msg, DiceJson diceJson) {
        UserDataCache user = userSession.getCache();

        if (diceJson != null) {
            if (diceJson.getBotDiceValue() != null) {
                user.setBotDice(diceJson.getBotDiceValue());
            } else {
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

                return createSendDiceResponse(responseString, generateDiceArray());
            }
        } else if (msg.equals(Phrases.DICE_QUIT_MSG)) {
            user.setMenu(Menu.MAIN_MENU);
            return createSendTextResponse(Phrases.DICE_OK_MSG);
        }
        return createSendDiceResponse(Phrases.DICE_ERROR_MSG, generateDiceArray());
    }

    private ShakalServiceRs horoscope_askForTitle() {
        userSession.getCache().setMenu(Menu.HOROSCOPE);
        return createSendKeyboardResponse(Phrases.HOROSCOPE_QUES_MSG, generateHoroscopeArray());
    }

    private ShakalServiceRs horoscope_process(String msg) {
        String sign = Phrases.HOROSCOPE_SIGNS.get(msg);
        if (sign == null) {
            return createSendKeyboardResponse(Phrases.HOROSCOPE_ERROR_MSG, generateHoroscopeArray());
        } else {
            userSession.getCache().setMenu(Menu.MAIN_MENU);
            return createSendTextResponse(generateHoroscope(sign));
        }
    }

    private ShakalServiceRs error() {
        return createSendTextResponse(Phrases.INVALID_COMMAND_MSG);
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

    private void updateDatabase(ShakalServiceRq shakalServiceRq) {
        UserJson userJson = shakalServiceRq.getUserJson();
        MessageJson messageJson = shakalServiceRq.getMessageJson();

        if (userJson != null) {
            userRepository.save(
                    new UserModel(
                            userJson.getId(),
                            shakalServiceRq.getChatId(),
                            userJson.getUsername(),
                            userJson.getFirstName(),
                            userJson.getLastName()
                    )
            );
        }

        if (messageJson != null) {
            requestRepository.save(
                    new RequestModel(
                            0L,
                            userJson.getId(),
                            messageJson.getTextMessage(),
                            new Date(messageJson.getDate().getTime() * 1000L),
                            new Time(messageJson.getDate().getTime() * 1000L)
                    )
            );
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

    private ShakalServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return ShakalServiceRs
                .builder()
                .shakalServiceTask(SHAKAL_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    private ShakalServiceRs createSendKeyboardResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return ShakalServiceRs
                .builder()
                .shakalServiceTask(SHAKAL_SERVICE_TASK.SEND_KEYBOARD)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();
    }

    private ShakalServiceRs createSendDiceResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return ShakalServiceRs
                .builder()
                .shakalServiceTask(SHAKAL_SERVICE_TASK.SEND_DICE)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();
    }
}
