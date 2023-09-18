package mavmi.telegram_bot.rocket_bot.telegram_bot;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.auth.BotNames;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RocketUserModel;
import mavmi.telegram_bot.common.database.repository.RocketUserRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.rocket_bot.httpHandler.HttpHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.LoginResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.MeResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static mavmi.telegram_bot.rocket_bot.constants.Levels.*;
import static mavmi.telegram_bot.rocket_bot.constants.Phrases.*;
import static mavmi.telegram_bot.rocket_bot.constants.Requests.*;

public class Bot extends AbsTelegramBot {
    private final TelegramBot telegramBot;
    private final UserAuthentication userAuthentication;
    private final RocketUserRepository rocketUserRepository;
    private final HttpHandler httpHandler;
    private final Long adminId;
    private final Map<Long, LocalUser> localUsers;

    public Bot(String telegramBotToken, UserAuthentication userAuthentication, RocketUserRepository rocketUserRepository, HttpHandler httpHandler, Logger logger, Long adminId) {
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.userAuthentication = userAuthentication;
        this.rocketUserRepository = rocketUserRepository;
        this.httpHandler = httpHandler;
        this.adminId = adminId;
        localUsers = new HashMap<>();
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
                logEvent(message);
                LocalUser localUser = processLocalUser(chatId);
                int userMenuLevel = localUser.getMenuLevel();

                if (userMenuLevel == MAIN_MENU_LEVEL) {
                    switch (inputText) {
                        case START_REQ -> start(localUser);
                        case LOGIN_REQ -> login(localUser, inputText);
                        case LOGOUT_REQ -> logout(localUser);
                        case ME_REQ -> me(localUser);
                        case FEEDBACK_REQ -> feedback(localUser, inputText);
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

    synchronized void sendMsg(SendMessage sendMessage){
        telegramBot.execute(sendMessage);
    }

    private void start(LocalUser localUser){
        sendMsg(new SendMessage(localUser.getChatId(), GREETINGS_MSG));
    }
    private void login(LocalUser localUser, String inputText) {
        int menuLevel = localUser.getMenuLevel();
        long chatId = localUser.getChatId();

        if (menuLevel == MAIN_MENU_LEVEL) {
            sendMsg(new SendMessage(chatId, ENTER_LOGIN_DATA_MSG));
            localUser.setMenuLevel(ENTER_LOGIN_DATA_LEVEL);
        } else if (menuLevel == ENTER_LOGIN_DATA_LEVEL) {
            String[] loginPasswd = inputText.split("\n");
            if (loginPasswd.length != 2) {
                sendMsg(new SendMessage(chatId, INVALID_DATA_FORMAT_MSG));
            } else {
                String username = loginPasswd[0];
                String passwd = loginPasswd[1];
                if (auth(username, passwd, chatId)) {
                    sendMsg(new SendMessage(chatId, LOGIN_SUCCESS_MSG));
                } else {
                    sendMsg(new SendMessage(chatId, LOGIN_FAIL_MSG));
                }
            }
            localUser.setMenuLevel(MAIN_MENU_LEVEL);
        }
    }
    private void logout(LocalUser localUser) {
        long chatId = localUser.getChatId();

        rocketUserRepository.delete(chatId);
        sendMsg(new SendMessage(chatId, LOGOUT_SUCCESS_MSG));
    }
    private void me(LocalUser localUser) {
        long chatId = localUser.getChatId();

        if (!preExecutionCheckout(chatId)) {
            sendMsg(new SendMessage(chatId, EXECUTION_FAIL_MSG));
        } else {
            RocketUserModel rocketUserModel = getRocketUserById(chatId);
            MeResponse meResponse = httpHandler.me(rocketUserModel.getRc_uid(), rocketUserModel.getRc_token());
            sendMsg(new SendMessage(chatId, meResponse.toString()));
        }
    }
    private void feedback(LocalUser localUser, String inputText) {
        int menuLevel = localUser.getMenuLevel();
        long chatId = localUser.getChatId();

        if (menuLevel == MAIN_MENU_LEVEL) {
            sendMsg(new SendMessage(chatId, ENTER_FEEDBACK_MSG));
            localUser.setMenuLevel(ENTER_FEEDBACK_LEVEL);
        } else if (menuLevel == ENTER_FEEDBACK_LEVEL) {
            sendMsg(new SendMessage(
                    adminId,
                    "Новый комментарий от пользователя:\n\n" + inputText
            ));
            sendMsg(new SendMessage(chatId, THX_MSG));
            localUser.setMenuLevel(MAIN_MENU_LEVEL);
        }
    }
    private void cancel(LocalUser localUser) {
        localUser.setMenuLevel(MAIN_MENU_LEVEL);
        sendMsg(new SendMessage(localUser.getChatId(), CANCEL_MSG));
    }

    private boolean preExecutionCheckout(long userId) {
        RocketUserModel rocketUserModel = getRocketUserById(userId);
        if (rocketUserModel == null) {
            return false;
        }
        if (isTokenActive(rocketUserModel.getToken_exp())) {
            return true;
        }
        return auth(rocketUserModel.getLogin(), rocketUserModel.getPasswd(), userId);
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

    private LocalUser processLocalUser(long chatId){
        LocalUser localUser = localUsers.get(chatId);

        if (localUser == null){
            localUser = new LocalUser(chatId, MAIN_MENU_LEVEL);
            localUsers.put(chatId, localUser);
        }

        return localUser;
    }
}
