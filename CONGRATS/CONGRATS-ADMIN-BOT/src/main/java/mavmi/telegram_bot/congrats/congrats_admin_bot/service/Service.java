package mavmi.telegram_bot.congrats.congrats_admin_bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.congrats.congrats_admin_bot.constants.Phrases;
import mavmi.telegram_bot.congrats.congrats_admin_bot.constants.Requests;
import mavmi.telegram_bot.congrats.utils.database.model.MessageModel;
import mavmi.telegram_bot.congrats.utils.database.model.UserModel;
import mavmi.telegram_bot.congrats.utils.database.repository.MessageRepository;
import mavmi.telegram_bot.congrats.utils.database.repository.RequestRepository;
import mavmi.telegram_bot.congrats.utils.database.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Service extends AbsService {
    private final MessageRepository messageRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public Service(
            MessageRepository messageRepository,
            RequestRepository requestRepository,
            UserRepository userRepository
    ) {
        this.messageRepository = messageRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handleRequest(Message telegramMessage) {
        User telegramUser = telegramMessage.from();

        long chatId = telegramUser.id();
        String username = telegramUser.username();
        String firstName = telegramUser.firstName();
        String lastName = telegramUser.lastName();
        String msg = telegramMessage.text();

        ServiceUser user = getUser(chatId, username, firstName, lastName);

        log.info("New request. id: {}; username: {}; first name: {}; last name: {}; message: {}", chatId, username, firstName, lastName, msg);
        if (!isAdmin(user)) {
            log.error("Access denied! id: {}", user.getUserId());
            return;
        }
        if (msg == null) {
            log.error("Message is NULL! id: {}", user.getUserId());
            return;
        }

        IMenu userMenu = user.getMenu();
        if (msg.equals(Requests.CANCEL_REQ)) {
            cancelOperation(user);
        } else if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case (Requests.ADD_MSG_REQ) -> addMsg_init(user);
                case (Requests.GET_ALL_MSGS_REQ) -> getAllMsgs(user);
                case (Requests.RM_MSG_REQ) -> rmMsg_init(user);
                case (Requests.STATUS_REQ) -> {
                    // TODO
                }
                case (Requests.CONTINUE_REQ) -> {
                    // TODO
                }
                case (Requests.PAUSE_REQ) -> {
                    // TODO
                }
            }
        } else if (userMenu == Menu.ADD_MSG) {
            addMsg_add(user, msg);
        } else if (userMenu == Menu.RM_MSG) {
            rmMsg_rm(user, msg);
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
        StringBuilder stringBuilder = new StringBuilder();
        List<MessageModel> messageModelList = messageRepository.getAll();

        if (messageModelList.isEmpty()) {
            telegramBot.sendMessage(user.getUserId(), Phrases.EMPTY_MESSAGES_LIST_MSG);
        } else {
            for (int i = 0; i < messageModelList.size(); i++) {
                stringBuilder.append(i + 1)
                        .append(") ")
                        .append(messageModelList.get(i).getMessage())
                        .append("\n\n");
            }

            telegramBot.sendMessage(user.getUserId(), stringBuilder.toString());
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
}
