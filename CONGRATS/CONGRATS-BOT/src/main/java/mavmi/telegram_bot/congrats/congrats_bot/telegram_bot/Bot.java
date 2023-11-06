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
import mavmi.telegram_bot.congrats.congrats_bot.constants.Requests;
import mavmi.telegram_bot.congrats.utils.database.model.UserModel;
import mavmi.telegram_bot.congrats.utils.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
    private final String filesVolPath;

    public Bot(
            UserRepository userRepository,
            @Value("${bot.token}") String botToken,
            @Value("${bot.files-vol}") String filesVolPath
    ) {
        super(null, botToken);
        this.userRepository = userRepository;
        this.filesVolPath = filesVolPath;
    }

    @Override
    @PostConstruct
    public void run() {
        log.info("CONGRATS-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                Message telegramMessage = update.message();
                User telegramUser = telegramMessage.from();

                long id = telegramMessage.from().id();
                String msg = telegramMessage.text();
                UserModel userModel = getUser(id);

                log.info("New request; id: {}", telegramUser.id());

                if (userModel == null) {
                    addUser(
                            telegramUser.id(),
                            telegramMessage.chat().id(),
                            telegramUser.username(),
                            telegramUser.firstName(),
                            telegramUser.lastName()
                    );
                } else if (msg != null && msg.equals(Requests.CH_REQ)) {
                    ch(userModel);
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            e.printStackTrace(System.err);
        });
    }

    public void sendVoice(String filePath) {
        byte[] fileData = uploadFile(filePath);

        if (fileData == null) {
            log.error("Cannot read voice file: {}", filePath);
            return;
        }

        for (Long id : getAllIdx()) {
            log.info("Voice message was sent to id: {}", id);
            telegramBot.execute(new SendVoice(id, fileData));
        }
    }

    public void sendVideoNote(String filePath) {
        byte[] fileData = uploadFile(filePath);

        if (fileData == null) {
            log.error("Cannot read video note file: {}", filePath);
            return;
        }

        for (Long id : getAllIdx()) {
            log.info("Video note message was sent to id: {}", id);
            telegramBot.execute(new SendVideoNote(id, fileData));
        }
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

    @Nullable
    private UserModel getUser(long id) {
        return userRepository.get(id);
    }

    @Nullable
    private byte[] uploadFile(String filePath) {
        int readCount = 0;
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        List<Byte> byteList = new ArrayList<>();

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            while ((readCount = inputStream.read(buffer, 0, bufferSize)) != -1) {
                for (int i = 0; i < readCount; i++) {
                    byteList.add(buffer[i]);
                }
            }

            byte[] res = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                res[i] = byteList.get(i);
            }

            return res;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
