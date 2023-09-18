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
import mavmi.telegram_bot.common.database.model.RocketImModel;
import mavmi.telegram_bot.common.database.model.RocketUserModel;
import mavmi.telegram_bot.common.database.repository.RocketImRepository;
import mavmi.telegram_bot.common.database.repository.RocketUserRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.rocket_bot.httpHandler.HttpHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.ImHistoryResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.ImListResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.LoginResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.MeResponse;

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

        Map<String, String> chatIdToKnowMsgId = new HashMap<>();
        for (RocketImModel rocketImModel : rocketImRepository.get(rcUid)) {
            chatIdToKnowMsgId.put(rocketImModel.getChat_id(), rocketImModel.getLast_msg_id());
        }

        List<ImListResponse> newMessages = httpHandler.imList(rcUid, rcToken, chatIdToKnowMsgId);
        if (newMessages == null) {
            return;
        }

        for (ImListResponse newMessage : newMessages) {
            if (notifyUsername) {
                List<ImHistoryResponse> historyResponses = newMessage.getHistoryResponses();
                if (rocketUserModel.getShow_content()) {
                    for (ImHistoryResponse unreadMessage : historyResponses) {
                        String msg = "***" +
                                " > " +
                                unreadMessage.getAuthor_name() +
                                " [" +
                                unreadMessage.getTimestamp() +
                                "]" +
                                ": " +
                                "***" +
                                "\n" +
                                unreadMessage.getMsg();
                        sendMsg(chatId, msg);
                    }
                } else {
                    if (!historyResponses.isEmpty()) {
                        sendMsg(
                                chatId,
                                newMessage.getHistoryResponses().size() +
                                        " новых сообщение от " +
                                        newMessage.getLast_msg_author_name()
                        );
                    }
                }
            }

            rocketImRepository.add(
                    RocketImModel.builder()
                            .rc_uid(newMessage.getRc_uid())
                            .chat_id(newMessage.getChat_id())
                            .last_msg_id(newMessage.getLast_msg_id())
                            .last_msg_author_id(newMessage.getLast_msg_author_id())
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
            rocketImRepository.delete(rocketUserModel.getRc_uid());
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
            MeResponse meResponse = httpHandler.me(rocketUserModel.getRc_uid(), rocketUserModel.getRc_token());
            if (meResponse != null) {
                sendMsg(
                        chatId,
                "***Username: ***" + meResponse.getUsername() + "\n" +
                        "***Email: ***" + meResponse.getEmail() + "\n" +
                        "***Имя: ***" + meResponse.getName() + "\n" +
                        "***Статус профиля: ***" + meResponse.getStatusText() + "\n" +
                        "***Статус подключения: ***" + meResponse.getStatusConnection()
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
        LoginResponse loginResponse = httpHandler.auth(username, passwd);
        if (loginResponse == null) {
            return false;
        }
        JsonObject message = loginResponse.getMessage();
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
}
