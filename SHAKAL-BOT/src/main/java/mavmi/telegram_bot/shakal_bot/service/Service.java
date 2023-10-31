package mavmi.telegram_bot.shakal_bot.service;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.shakal_bot.constants.DicePhrases;
import mavmi.telegram_bot.shakal_bot.constants.Goose;
import mavmi.telegram_bot.shakal_bot.constants.Phrases;
import mavmi.telegram_bot.shakal_bot.constants.Requests;
import mavmi.telegram_bot.shakal_bot.telegram_bot.Bot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Slf4j
@Component
public class Service extends AbsService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public Service(
            UserRepository userRepository,
            RequestRepository requestRepository
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public void handleRequest(Message telegramMessage) {
        updateDatabase(telegramMessage);

        User telegramUser = telegramMessage.from();
        long chatId = telegramUser.id();
        String username = telegramUser.username();
        String firstName = telegramUser.firstName();
        String lastName = telegramUser.lastName();
        String msg = telegramMessage.text();

        ServiceUser user = getUser(chatId, username, firstName, lastName);
        log.info("New request. id: {}; username: {}; first name: {}; last name: {}; message: {}", chatId, username, firstName, lastName, msg);
        IMenu userMenu = user.getMenu();

        if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case (Requests.START_REQ) -> greetings(chatId);
                case (Requests.APOLOCHEESE_REQ) -> apolocheese_askForName(user);
                case (Requests.GOOSE_REQ) -> goose(user);
                case (Requests.ANEK_REQ) -> anek(user);
                case (Requests.MEME_REQ) -> meme(user);
                case (Requests.DICE_REQ) -> dice_init(user);
                case (Requests.HOROSCOPE_REQ) ->  horoscope_askForTitle(user);
                default -> error(user);
            }
        } else if (userMenu == Menu.APOLOCHEESE) {
            apolocheese_process(user, msg);
        } else if (userMenu == Menu.DICE) {
            dice_play(user, telegramMessage);
        } else if (userMenu == Menu.HOROSCOPE) {
            horoscope_process(user, msg);
        }
    }

    private void greetings(long chatId) {
        telegramBot.sendMessage(chatId, Phrases.GREETINGS_MSG);
    }

    private void apolocheese_askForName(ServiceUser user) {
        user.setMenu(Menu.APOLOCHEESE);
        telegramBot.sendMessage(user.getUserId(), Phrases.APOLOCHEESE_MSG);
    }

    private void apolocheese_process(ServiceUser user, String msg) {
        telegramBot.sendMessage(user.getUserId(), generateApolocheese(msg), ParseMode.Markdown);
        user.setMenu(Menu.MAIN_MENU);
    }

    private void goose(ServiceUser user) {
        telegramBot.sendMessage(user.getUserId(), generateGoose());
    }

    private void anek(ServiceUser user) {
        telegramBot.sendMessage(user.getUserId(), generateAnek());
    }

    private void meme(ServiceUser user) {
        PendingRequest request = Memes4J.getRandomMeme();

        try {
            telegramBot.sendMessage(user.getUserId(), request.complete().getImage());
        } catch (Exception e) {
            telegramBot.sendMessage(user.getUserId(), Phrases.EXCEPTION_MSG);
            e.printStackTrace(System.err);
        }
    }

    private void dice_init(ServiceUser user) {
        user.setMenu(Menu.DICE);
        user.setBotDice(
                telegramBot.sendRequest(
                        new SendDice(user.getUserId()).replyMarkup(Bot.getDiceKeyboard()))
                        .message()
                        .dice()
                        .value()
        );
    }

    private void dice_play(ServiceUser user, Message telegramMessage) {
        if (telegramMessage.dice() != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }

                user.setUserDice(telegramMessage.dice().value());
                if (user.getUserDice() > user.getBotDice()) {
                    telegramBot.sendMessage(user.getUserId(), DicePhrases.getRandomWinPhrase());
                } else if (user.getUserDice() < user.getBotDice()) {
                    telegramBot.sendMessage(user.getUserId(), DicePhrases.getRandomLosePhrase());
                }
                user.setBotDice(
                        telegramBot.sendRequest(
                                        new SendDice(user.getUserId()).replyMarkup(Bot.getDiceKeyboard()))
                                .message()
                                .dice()
                                .value()
                );
            }).start();
        } else if (telegramMessage.text().equals(Phrases.DICE_QUIT_MSG)) {
            telegramBot.sendMessage(user.getUserId(), Phrases.DICE_OK_MSG);
            user.setMenu(Menu.MAIN_MENU);
        } else {
            telegramBot.sendMessage(
                    new SendMessage(user.getUserId(), Phrases.DICE_ERROR_MSG)
                            .replyMarkup(Bot.getDiceKeyboard())
            );
        }
    }

    private void horoscope_askForTitle(ServiceUser user) {
        user.setMenu(Menu.HOROSCOPE);
        telegramBot.sendMessage(
                new SendMessage(user.getUserId(), Phrases.HOROSCOPE_QUES_MSG)
                        .replyMarkup(Bot.getHoroscopeKeyboard()));
    }

    private void horoscope_process(ServiceUser user, String msg) {
        String sign = Phrases.HOROSCOPE_SIGNS.get(msg);
        if (sign == null) {
            telegramBot.sendMessage(
                    new SendMessage(user.getUserId(), Phrases.HOROSCOPE_ERROR_MSG)
                            .replyMarkup(Bot.getHoroscopeKeyboard()));
        } else {
            telegramBot.sendMessage(user.getUserId(), generateHoroscope(sign));
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
            e.printStackTrace(System.err);
            return Phrases.EXCEPTION_MSG;
        }
    }

    private String generateHoroscope(String sign) {
        try {
            Document document = Jsoup
                    .connect("https://horo.mail.ru/prediction/" + sign + "/today/")
                    .get();
            StringBuilder builder = new StringBuilder();

            for (Element element : document.getElementsByTag("p")) {
                if (!builder.isEmpty()) {
                    builder.append("\n").append("\n");
                }
                builder.append(element.text());
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return Phrases.EXCEPTION_MSG;
        }
    }

    private void error(ServiceUser user) {
        telegramBot.sendMessage(user.getUserId(), Phrases.INVALID_COMMAND_MSG);
    }

    private ServiceUser getUser(Long chatId, String username, String firstName, String lastName) {
        ServiceUser user = (ServiceUser) idToUser.get(chatId);

        if (user == null) {
            user = new ServiceUser(chatId, Menu.MAIN_MENU, username, firstName, lastName);
            idToUser.put(chatId, user);
        }

        return user;
    }

    private void updateDatabase(Message telegramMessage) {
        com.pengrad.telegrambot.model.User telegramUser = telegramMessage.from();

        userRepository.add(new UserModel(
                telegramUser.id(),
                telegramMessage.chat().id(),
                telegramUser.username(),
                telegramUser.firstName(),
                telegramUser.lastName()
        ));

        requestRepository.add(new RequestModel(
                telegramUser.id(),
                telegramMessage.text(),
                new Date((long) telegramMessage.date() * 1000L),
                new Time((long) telegramMessage.date() * 1000L)
        ));
    }
}
