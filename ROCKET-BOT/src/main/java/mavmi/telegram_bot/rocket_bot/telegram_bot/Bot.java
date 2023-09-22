package mavmi.telegram_bot.rocket_bot.telegram_bot;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.auth.BotNames;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RocketGroupsModel;
import mavmi.telegram_bot.common.database.model.RocketImModel;
import mavmi.telegram_bot.common.database.model.RocketUserModel;
import mavmi.telegram_bot.common.database.repository.RocketGroupsRepository;
import mavmi.telegram_bot.common.database.repository.RocketImRepository;
import mavmi.telegram_bot.common.database.repository.RocketUserRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.rocket_bot.httpHandler.HttpHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.*;
import org.springframework.lang.Nullable;

import java.util.*;

import static mavmi.telegram_bot.rocket_bot.constants.Levels.*;
import static mavmi.telegram_bot.rocket_bot.constants.Phrases.*;
import static mavmi.telegram_bot.rocket_bot.constants.Requests.*;

public class Bot extends AbsTelegramBot {
    private final TelegramBot telegramBot;
    private final Long sleepTime;
    private final UserAuthentication userAuthentication;
    private final RocketUserRepository rocketUserRepository;
    private final RocketImRepository rocketImRepository;
    private final RocketGroupsRepository rocketGroupsRepository;
    private final HttpHandler httpHandler;
    private final Long adminId;
    private final Map<Long, LocalUser> localUsers;
    private final Notifier notifier;

    public Bot(
            String telegramBotToken,
            Long sleepTime,
            UserAuthentication userAuthentication,
            RocketUserRepository rocketUserRepository,
            RocketImRepository rocketImRepository,
            RocketGroupsRepository rocketGroupsRepository,
            HttpHandler httpHandler,
            Logger logger,
            Long adminId
    ) {
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.sleepTime = sleepTime;
        this.userAuthentication = userAuthentication;
        this.rocketUserRepository = rocketUserRepository;
        this.rocketImRepository = rocketImRepository;
        this.rocketGroupsRepository = rocketGroupsRepository;
        this.httpHandler = httpHandler;
        this.adminId = adminId;
        this.localUsers = new HashMap<>();
        this.notifier = startThread();
    }

    @Override
    public void run() {
        logger.log("ROCKET-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                Message message = update.message();
                long userid = message.from().id();
                long chatId = message.chat().id();
                String inputText = message.text();

                if (!userAuthentication.isPrivilegeGranted(userid, BotNames.ROCKET_BOT)) continue;
                if (inputText == null) continue;
                LocalUser localUser = processLocalUser(chatId);
                int userMenuLevel = localUser.getMenuLevel();

                if (userMenuLevel == MAIN_MENU_LEVEL) {
                    if (!inputText.equals(FEEDBACK_REQ)) logEvent(message);
                    switch (inputText) {
                        case START_REQ -> start(localUser);
                        case LOGIN_REQ -> login(localUser, inputText);
                        case LOGOUT_REQ -> logout(localUser);
                        case ME_REQ -> me(localUser);
                        case FEEDBACK_REQ -> feedback(localUser, inputText);
                        case SHOW_CONTENT_REQ -> showContent(localUser);
                    }
                } else {
                    if (inputText.equals(CANCEL_REQ)) {
                        cancel(localUser);
                        continue;
                    }

                    if (userMenuLevel == ENTER_LOGIN_DATA_LEVEL) {
                        login(localUser, inputText);
                    } else if (userMenuLevel == ENTER_FEEDBACK_LEVEL) {
                        feedback(localUser, inputText);
                    }
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    synchronized void sendMsg(long chatId, String message, ParseMode parseMode) {
        telegramBot.execute(new SendMessage(chatId, message).parseMode(parseMode));
    }
    synchronized void sendMsg(long chatId, String message) {
        sendMsg(chatId, message, ParseMode.Markdown);
    }
    synchronized void updateLastMessages(long chatId, boolean notifyUsername) {
        if (!preExecutionTokenCheckout(chatId)) {
            return;
        }

        RocketUserModel rocketUserModel = rocketUserRepository.get(chatId);
        String rcUid = rocketUserModel.getRc_uid();
        String rcToken = rocketUserModel.getRc_token();

        Map<String, String> chatIdToLastKnownMsgId = new HashMap<>();
        for (RocketImModel rocketImModel : rocketImRepository.get(rcUid)) {
            chatIdToLastKnownMsgId.put(rocketImModel.getChat_id(), rocketImModel.getLast_msg_id());
        }

        List<ImListJsonModel> imListJsonModels = httpHandler.imList(rcUid, rcToken, chatIdToLastKnownMsgId);
        if (imListJsonModels == null) {
            return;
        }

        for (ImListJsonModel imListJsonModel : imListJsonModels) {
            if (notifyUsername) {
                List<ImHistoryJsonModel> historyResponses = imListJsonModel.getHistoryResponses();
                if (rocketUserModel.getShow_content()) {
                    for (ImHistoryJsonModel imHistoryJsonModel : historyResponses) {
                        sendMsg(chatId, generateNotifyMessageString(imHistoryJsonModel.getMessage()));
                    }
                } else if (!historyResponses.isEmpty()) {
                    sendMsg(
                            chatId,
                            imListJsonModel.getHistoryResponses().size() +
                                    " новых сообщение от " +
                                    imListJsonModel.getLastMessage().getAuthor().getName()
                    );
                }
            }

            rocketImRepository.add(
                    RocketImModel.builder()
                            .rc_uid(rcUid)
                            .chat_id(imListJsonModel.getChatId())
                            .last_msg_id(imListJsonModel.getLastMessage().getId())
                            .last_msg_author_id(imListJsonModel.getLastMessage().getAuthor().getId())
                            .build()
            );
        }
    }
    synchronized void updateLastGroupMentions(long chatId, boolean notifyUsername) {
        if (!preExecutionTokenCheckout(chatId)) {
            return;
        }

        RocketUserModel rocketUserModel = rocketUserRepository.get(chatId);
        String rcUid = rocketUserModel.getRc_uid();
        String rcToken = rocketUserModel.getRc_token();

        Map<String, String> groupIdToLastKnownMentionMsgId = new HashMap<>();
        for (RocketGroupsModel rocketGroupsModel : rocketGroupsRepository.get(rcUid)) {
            groupIdToLastKnownMentionMsgId.put(rocketGroupsModel.getGroup_id(), rocketGroupsModel.getLast_msg_id());
        }

        List<GroupsListJsonModel> groupsListJsonModels = httpHandler.groupsList(rcUid, rcToken, groupIdToLastKnownMentionMsgId);
        if (groupsListJsonModels == null) {
            return;
        }

        for (GroupsListJsonModel groupsListJsonModel : groupsListJsonModels) {
            if (notifyUsername) {
                String groupName = groupsListJsonModel.getGroupName();
                List<GroupsHistoryJsonModel> historyResponses = groupsListJsonModel.getHistoryResponses();
                if (rocketUserModel.getShow_content()) {
                    for (GroupsHistoryJsonModel groupsHistoryJsonModel : historyResponses) {
                        sendMsg(
                                chatId,
                                generateNotifyMessageString(groupsHistoryJsonModel.getMessage(), groupName)
                        );
                    }
                } else if (!historyResponses.isEmpty()) {
                    sendMsg(
                            chatId,
                            groupsListJsonModel.getHistoryResponses().size() +
                                    " новых сообщение в группе " +
                                    groupName
                    );
                }
            }

            rocketGroupsRepository.add(
                    RocketGroupsModel.builder()
                            .rc_uid(rcUid)
                            .group_id(groupsListJsonModel.getGroupId())
                            .last_msg_id(groupsListJsonModel.getLastMessage().getId())
                            .last_msg_author_id(groupsListJsonModel.getLastMessage().getAuthor().getId())
                            .build()
            );
        }
    }

    private void start(LocalUser localUser){
        sendMsg(localUser.getChatId(), GREETINGS_MSG);
    }
    private void login(LocalUser localUser, String inputText) {
        int menuLevel = localUser.getMenuLevel();
        long chatId = localUser.getChatId();

        if (menuLevel == MAIN_MENU_LEVEL) {
            sendMsg(chatId, ENTER_LOGIN_DATA_MSG);
            localUser.setMenuLevel(ENTER_LOGIN_DATA_LEVEL);
        } else if (menuLevel == ENTER_LOGIN_DATA_LEVEL) {
            String[] loginPasswd = inputText.split("\n");
            if (loginPasswd.length != 2) {
                sendMsg(chatId, INVALID_DATA_FORMAT_MSG);
            } else {
                String username = loginPasswd[0];
                String passwd = loginPasswd[1];

                if (rocketUserRepository.get(username) != null) {
                    sendMsg(chatId, USER_DUPLICATE_MSG);
                } else if (auth(username, passwd, chatId)) {
                    updateLastMessages(chatId, false);
                    updateLastGroupMentions(chatId, false);
                    notifier.addUser(chatId);
                    sendMsg(chatId, LOGIN_SUCCESS_MSG);
                } else {
                    sendMsg(chatId, LOGIN_FAIL_MSG);
                }
            }
            localUser.setMenuLevel(MAIN_MENU_LEVEL);
        }
    }
    private void logout(LocalUser localUser) {
        long chatId = localUser.getChatId();
        RocketUserModel rocketUserModel = getRocketUserById(chatId);

        if (rocketUserModel != null) {
            String rcUid = rocketUserModel.getRc_uid();

            rocketImRepository.delete(rcUid);
            rocketGroupsRepository.delete(rcUid);
            rocketUserRepository.delete(chatId);
            notifier.deleteUser(chatId);

            sendMsg(chatId, LOGOUT_SUCCESS_MSG);
        }
    }
    private void me(LocalUser localUser) {
        long chatId = localUser.getChatId();

        if (!preExecutionTokenCheckout(chatId)) {
            sendMsg(chatId, EXECUTION_FAIL_MSG);
        } else {
            RocketUserModel rocketUserModel = getRocketUserById(chatId);
            MeJsonModel meJsonModel = httpHandler.me(rocketUserModel.getRc_uid(), rocketUserModel.getRc_token());
            if (meJsonModel != null) {
                sendMsg(
                        chatId,
                "***Username: ***" + meJsonModel.getUsername() + "\n" +
                        "***Email: ***" + meJsonModel.getEmail() + "\n" +
                        "***Имя: ***" + meJsonModel.getName() + "\n" +
                        "***Статус профиля: ***" + meJsonModel.getStatusText() + "\n" +
                        "***Статус подключения: ***" + meJsonModel.getStatusConnection()
                );
            }
        }
    }
    private void feedback(LocalUser localUser, String inputText) {
        int menuLevel = localUser.getMenuLevel();
        long chatId = localUser.getChatId();

        if (menuLevel == MAIN_MENU_LEVEL) {
            sendMsg(chatId, ENTER_FEEDBACK_MSG);
            localUser.setMenuLevel(ENTER_FEEDBACK_LEVEL);
        } else if (menuLevel == ENTER_FEEDBACK_LEVEL) {
            sendMsg(
                    adminId,
                    "Новый комментарий от пользователя:\n\n" + inputText
            );
            sendMsg(chatId, THX_MSG);
            localUser.setMenuLevel(MAIN_MENU_LEVEL);
        }
    }
    private void showContent(LocalUser localUser) {
        long chatId = localUser.getChatId();
        RocketUserModel rocketUserModel = rocketUserRepository.get(chatId);

        if (rocketUserModel != null) {
            boolean newValue = !rocketUserModel.getShow_content();
            rocketUserModel.setShow_content(newValue);
            rocketUserRepository.add(rocketUserModel);
            sendMsg(
                    chatId,
                    (newValue) ? "включено" : "выключено"
            );
        }
    }
    private void cancel(LocalUser localUser) {
        localUser.setMenuLevel(MAIN_MENU_LEVEL);
        sendMsg(localUser.getChatId(), CANCEL_MSG);
    }

    private RocketUserModel getRocketUserById(long userId) {
        return rocketUserRepository.get(userId);
    }
    private boolean isTokenActive(Long date) {
        Date tokenDate = new Date(date);
        Date currentDate = new Date();

        return tokenDate.compareTo(currentDate) > 0;
    }
    private boolean auth(String username, String passwd, long chatId) {
        LoginJsonModel loginJsonModel = httpHandler.auth(username, passwd);
        if (loginJsonModel == null) {
            return false;
        }
        JsonObject message = loginJsonModel.getMessage();
        if (message.has("result")) {
            JsonObject result = message.get("result").getAsJsonObject();
            String id = result.get("id").getAsString();
            String token = result.get("token").getAsString();
            Long exp = result.get("tokenExpires").getAsJsonObject().get("$date").getAsLong();
            rocketUserRepository.add(
                    RocketUserModel.builder()
                            .userid(chatId)
                            .login(username)
                            .passwd(passwd)
                            .rc_uid(id)
                            .rc_token(token)
                            .token_exp(exp)
                            .build()
            );
            return true;
        } else {
            return false;
        }
    }
    private boolean preExecutionTokenCheckout(long userId) {
        RocketUserModel rocketUserModel = getRocketUserById(userId);

        if (rocketUserModel == null) {
            return false;
        }
        if (isTokenActive(rocketUserModel.getToken_exp())) {
            return true;
        }

        return auth(rocketUserModel.getLogin(), rocketUserModel.getPasswd(), userId);
    }

    private LocalUser processLocalUser(long chatId){
        LocalUser localUser = localUsers.get(chatId);

        if (localUser == null){
            localUser = new LocalUser(chatId, MAIN_MENU_LEVEL);
            localUsers.put(chatId, localUser);
        }

        return localUser;
    }
    private Notifier startThread() {
        Set<Long> userIdx = new HashSet<>();

        for (RocketUserModel rocketUserModel : rocketUserRepository.getAll()) {
            userIdx.add(rocketUserModel.getUserid());
        }

        Notifier tmpNotifier = new Notifier(this, sleepTime, userIdx);
        tmpNotifier.start();

        return tmpNotifier;
    }
    private String generateNotifyMessageString(MessageJsonModel messageJsonModel) {
        return generateNotifyMessageString(messageJsonModel, null);
    }
    private String generateNotifyMessageString(MessageJsonModel messageJsonModel, @Nullable String groupName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("***")
                .append(" > ")
                .append(messageJsonModel.getAuthor().getName());

        if (groupName != null) {
            stringBuilder
                    .append("@")
                    .append(groupName);
        }

        return stringBuilder
                .append(" [")
                .append(messageJsonModel.getTimestamp())
                .append("]:")
                .append("***")
                .append("\n")
                .append(messageJsonModel.getText())
                .toString();
    }
}
