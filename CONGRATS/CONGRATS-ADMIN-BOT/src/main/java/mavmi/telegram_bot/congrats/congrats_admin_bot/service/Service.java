package mavmi.telegram_bot.congrats.congrats_admin_bot.service;

import com.pengrad.telegrambot.model.*;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.congrats.congrats_admin_bot.constants.Phrases;
import mavmi.telegram_bot.congrats.congrats_admin_bot.constants.Requests;
import mavmi.telegram_bot.congrats.congrats_admin_bot.telegram_bot.Bot;
import mavmi.telegram_bot.congrats.utils.database.model.MessageModel;
import mavmi.telegram_bot.congrats.utils.database.model.UserModel;
import mavmi.telegram_bot.congrats.utils.database.repository.MessageRepository;
import mavmi.telegram_bot.congrats.utils.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class Service extends AbsService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final String filesVolPath;

    public Service(
            MessageRepository messageRepository,
            UserRepository userRepository,
            @Value("${bot.files-vol}") String filesVolPath
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.filesVolPath = filesVolPath;
    }

    @Override
    public void handleRequest(Message telegramMessage) {
        User telegramUser = telegramMessage.from();

        long chatId = telegramUser.id();
        String username = telegramUser.username();
        String firstName = telegramUser.firstName();
        String lastName = telegramUser.lastName();

        String msg = telegramMessage.text();
        Voice voice = telegramMessage.voice();
        VideoNote videoNote = telegramMessage.videoNote();
        Contact telelgramContact = telegramMessage.contact();

        ServiceUser user = getUser(chatId, username, firstName, lastName);

        log.info("New request. id: {}; username: {}; first name: {}; last name: {}", chatId, username, firstName, lastName);
        if (!isAdmin(user)) {
            log.error("Access denied! id: {}", user.getUserId());
            return;
        }

        if (msg != null) {
            handleTextRequest(user, msg);
        } else if (voice != null) {
            handleVoiceRequest(voice);
        } else if (videoNote != null) {
            handleVideoNoteRequest(videoNote);
        } else if (telelgramContact != null) {
            handleContactRequest(telelgramContact);
        } else {
            log.error("Message is null");
        }
    }

    private void handleTextRequest(ServiceUser user, String msg) {
        log.info("Text request");

        IMenu userMenu = user.getMenu();
        if (msg.equals(Requests.CANCEL_REQ)) {
            cancelOperation(user);
        } else if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case (Requests.ADD_MSG_REQ) -> addMsg_init(user);
                case (Requests.GET_ALL_MSGS_REQ) -> getAllMsgs(user);
                case (Requests.RM_MSG_REQ) -> rmMsg_init(user);
            }
        } else if (userMenu == Menu.ADD_MSG) {
            addMsg_add(user, msg);
        } else if (userMenu == Menu.RM_MSG) {
            rmMsg_rm(user, msg);
        }
    }

    private void handleVoiceRequest(Voice voice) {
        log.info("Voice request");

        String path = downloadFile(voice.fileId(), "voice");
        if (path != null) {
            log.info("Voice message saved");
        } else {
            log.error("An error occurred during saving voice message");
        }
    }

    private void handleVideoNoteRequest(VideoNote videoNote) {
        log.info("Video note request");

        String path = downloadFile(videoNote.fileId(), "videoNote");
        if (path != null) {
            log.info("Video note saved");
        } else {
            log.error("An error occurred during saving video note");
        }
    }

    private void handleContactRequest(Contact contact) {
        UserModel userModel = UserModel.builder()
                .id(contact.userId())
                .chatId(contact.userId())
                .username(null)
                .firstName(contact.firstName())
                .lastName(contact.lastName())
                .admin(true)
                .intensive(false)
                .build();

        if (userRepository.get(userModel.getId()) == null) {
            userRepository.add(userModel);
        } else {
            userRepository.update(userModel);
        }
    }

    private void addMsg_init(ServiceUser user) {
        user.setMenu(Menu.ADD_MSG);
        telegramBot.sendMessage(user.getUserId(), Phrases.ADD_MSG);
    }

    private void addMsg_add(ServiceUser user, String msg) {
        messageRepository.add(
                MessageModel.builder()
                        .id(0L)
                        .message(msg)
                        .build()
        );
        dropUserInfo(user);
        telegramBot.sendMessage(user.getUserId(), Phrases.SUCCESS_MSG);
    }

    private int getAllMsgs(ServiceUser user) {
        List<MessageModel> messageModelList = messageRepository.getAll();

        if (messageModelList.isEmpty()) {
            telegramBot.sendMessage(user.getUserId(), Phrases.EMPTY_MESSAGES_LIST_MSG);
        } else {
            for (int i = 0; i < messageModelList.size(); i++) {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(i + 1)
                        .append(") ")
                        .append(messageModelList.get(i).getMessage());

                telegramBot.sendMessage(user.getUserId(), stringBuilder.toString());
            }

        }

        return messageModelList.size();
    }

    private void rmMsg_init(ServiceUser user) {
        if (getAllMsgs(user) != 0) {
            user.setMenu(Menu.RM_MSG);
            telegramBot.sendMessage(user.getUserId(), Phrases.RM_MSG);
        }
    }

    private void rmMsg_rm(ServiceUser user, String msg) {
        try {
            int pos = Integer.parseInt(msg);
            List<MessageModel> messageModelList = messageRepository.getAll();

            if (pos < 1 || pos > messageModelList.size()) {
                telegramBot.sendMessage(user.getUserId(), Phrases.ERROR_MSG);
            } else {
                messageRepository.delete(messageModelList.get(pos - 1));
                telegramBot.sendMessage(user.getUserId(), Phrases.SUCCESS_MSG);
            }
        } catch (NumberFormatException e) {
            telegramBot.sendMessage(user.getUserId(), Phrases.ERROR_MSG);
        } finally {
            dropUserInfo(user);
        }
    }

    private void cancelOperation(ServiceUser user) {
        dropUserInfo(user);
        telegramBot.sendMessage(user.getUserId(), Phrases.CANCEL_OPERATION_MSG);
    }

    private void dropUserInfo(ServiceUser user) {
        user.setMenu(Menu.MAIN_MENU);
        user.getLastMessages().clear();
    }

    private boolean isAdmin(ServiceUser user) {
        UserModel userModel = userRepository.get(user.getUserId());

        return userModel != null && userModel.getAdmin() != null && userModel.getAdmin();
    }

    private ServiceUser getUser(Long chatId, String username, String firstName, String lastName) {
        ServiceUser user = (ServiceUser) idToUser.get(chatId);

        if (user == null) {
            user = new ServiceUser(chatId, Menu.MAIN_MENU, username, firstName, lastName);
            idToUser.put(chatId, user);
        }

        return user;
    }

    @Nullable
    private String downloadFile(String fileId, String directory) {
        String url = ((Bot) telegramBot).getFileUrl(fileId);
        String filePath = filesVolPath +
                "/" +
                directory +
                "/" +
                UUID.randomUUID();
        BufferedInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            int readCount = 0;
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            inputStream = new BufferedInputStream(new URL(url).openStream());
            outputStream = new FileOutputStream(filePath);

            while ((readCount = inputStream.read(buffer, 0, bufferSize)) != -1) {
                outputStream.write(buffer, 0, readCount);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }

        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }

        log.info("File successfully saved: {}", filePath);
        return filePath;
    }
}
