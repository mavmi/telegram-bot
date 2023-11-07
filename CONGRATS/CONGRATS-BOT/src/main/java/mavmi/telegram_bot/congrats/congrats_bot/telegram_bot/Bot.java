package mavmi.telegram_bot.congrats.congrats_bot.telegram_bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendVideoNote;
import com.pengrad.telegrambot.request.SendVoice;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.congrats.congrats_bot.constants.Buttons;
import mavmi.telegram_bot.congrats.congrats_bot.constants.Phrases;
import mavmi.telegram_bot.congrats.congrats_bot.constants.Requests;
import mavmi.telegram_bot.congrats.utils.database.model.RequestModel;
import mavmi.telegram_bot.congrats.utils.database.model.UserModel;
import mavmi.telegram_bot.congrats.utils.database.repository.RequestRepository;
import mavmi.telegram_bot.congrats.utils.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Bot extends AbsTelegramBot {
    private final static ReplyKeyboardMarkup studyDirectionKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);
    static {
        studyDirectionKeyboard.addRow(Buttons.INTENSIVE_BTN);
        studyDirectionKeyboard.addRow(Buttons.BASIC_BTN);
    }

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public Bot(
            UserRepository userRepository,
            RequestRepository requestRepository,
            @Value("${bot.token}") String botToken
    ) {
        super(null, botToken);
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    @PostConstruct
    public void run() {
        log.info("CONGRATS-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                Message telegramMessage = update.message();
                if (telegramMessage == null) {
                    continue;
                }

                User telegramUser = telegramMessage.from();
                long id = telegramUser.id();
                String msg = telegramMessage.text();
                log.info("New request; id: {}", telegramUser.id());

                addUser(
                        telegramUser.id(),
                        telegramMessage.chat().id(),
                        telegramUser.username(),
                        telegramUser.firstName(),
                        telegramUser.lastName()
                );

                addRequest(
                        telegramUser.id(),
                        telegramMessage.text(),
                        telegramMessage.date()
                );

                if (msg == null) {
                    continue;
                } else if (msg.equals(Requests.START_REQ)) {
                    sendMessage(id, Phrases.START_MSG);
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            e.printStackTrace(System.err);
        });
    }

    public void sendVoice(long id, byte[] fileData) {
        log.info("Voice message was sent to id: {}", id);
        telegramBot.execute(new SendVoice(id, fileData));
    }

    public void sendVideoNote(long id, byte[] fileData) {
        log.info("Video note message was sent to id: {}", id);
        telegramBot.execute(new SendVideoNote(id, fileData));
    }

    public List<Long> getAllIdx() {
        List<Long> idx = new ArrayList<>();

        for (UserModel userModel : userRepository.getAll()) {
            idx.add(userModel.getId());
        }

        return idx;
    }

    private void ch(UserModel userModel) {
        boolean newValue = !userModel.getIntensive();
        userModel.setIntensive(newValue);
        userRepository.update(userModel);
        sendMessage(
                userModel.getChatId(),
                "Твоя программа обучения изменена на " + ((newValue) ? "интенсив" : "основу")
        );
    }

    private void addUser(
            long id,
            long chatId,
            String username,
            String firstName,
            String lastName
    ) {
        UserModel userModel = UserModel.builder()
                .id(id)
                .chatId(chatId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .intensive(true)
                .admin(false)
                .build();

        userRepository.add(userModel);
    }

    private void addRequest(
            long userId,
            String msg,
            int date
    ) {
        RequestModel requestModel = RequestModel.builder()
                .userId(userId)
                .message(msg)
                .date(new Date((long) date * 1000L))
                .time(new Time((long) date * 1000L))
                .build();

        requestRepository.add(requestModel);
    }

    @Nullable
    private UserModel getUser(long id) {
        return userRepository.get(id);
    }
}
