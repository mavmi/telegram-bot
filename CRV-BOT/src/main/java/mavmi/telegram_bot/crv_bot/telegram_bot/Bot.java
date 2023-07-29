package mavmi.telegram_bot.crv_bot.telegram_bot;

import com.google.gson.JsonParser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AccessLevel;
import lombok.Getter;
import mavmi.telegram_bot.common.auth.BotNames;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.repository.CrvRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import mavmi.telegram_bot.crv_bot.user.CrvProfile;
import okhttp3.OkHttpClient;

import java.sql.Array;
import java.util.HashMap;
import java.util.Map;

import static mavmi.telegram_bot.crv_bot.constants.Requests.AUTO_REQUEST;
import static mavmi.telegram_bot.crv_bot.constants.Requests.GET_COUNT_REQUEST;

@Getter(value = AccessLevel.PACKAGE)
public class Bot extends AbsTelegramBot {
    private final OkHttpClient okHttpClient;
    private final TelegramBot telegramBot;
    private final RequestOptions requestOptions;
    private final UserAuthentication userAuthentication;
    private final CrvRepository crvRepository;

    private final Map<Long, Checker> checkerList = new HashMap<>();

    public Bot(String telegramBotToken, Logger logger, RequestOptions requestOptions, UserAuthentication userAuthentication, CrvRepository crvRepository){
        super(logger);
        this.okHttpClient = new OkHttpClient();
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.requestOptions = requestOptions;
        this.userAuthentication = userAuthentication;
        this.crvRepository = crvRepository;
    }
    
    @Override
    public void run(){
        logger.log("CRV-BOT IS RUNNING");
        initCheckerList();
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                Message message = update.message();
                long userId = message.chat().id();
                String clientMsg = message.text();

                if (!userAuthentication.isPrivilegeGranted(userId, BotNames.CRV_BOT)) continue;
                CrvProfile crvProfile = CrvProfile.getCrvProfile(crvRepository, userId);
                logEvent(message);
                switch (clientMsg) {
                    case (GET_COUNT_REQUEST) -> checkCrvCount(crvProfile);
                    case (AUTO_REQUEST) -> auto(crvProfile);
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    synchronized void sendMsg(long id, String msg){
        telegramBot.execute(new SendMessage(id, msg));
    }

    void checkCrvCount(CrvProfile crvProfile){
        try {
            String response = okHttpClient.newCall(crvProfile.getCrvCountRequest(requestOptions)).execute().body().string();
            logger.log("RESPONSE: " + response);
            int i = JsonParser.parseString(response)
                    .getAsJsonObject()
                    .get(requestOptions.getJsonFields().get(0))
                    .getAsJsonObject()
                    .get(requestOptions.getJsonFields().get(1))
                    .getAsJsonObject()
                    .get(requestOptions.getJsonFields().get(2))
                    .getAsJsonArray()
                    .size();

            sendMsg(crvProfile.getCrvModel().getId(), Integer.toString(i));
            Array array = crvProfile.getCrvModel().getRedirect();
            if (array == null || i == 0) return;
            for (long idx : (Long[]) array.getArray()){
                sendMsg(idx, Integer.toString(i));
            }
        } catch (Exception e) {
            logger.err(e.getMessage());
            sendMsg(crvProfile.getCrvModel().getId(), "BOT_ERROR");
        }
    }
    void auto(CrvProfile crvProfile){
        boolean value = !crvProfile.getCrvModel().getAuto();
        crvProfile.getCrvModel().setAuto(value);

        Checker oldChecker = checkerList.get(crvProfile.getCrvModel().getId());
        if (oldChecker != null) oldChecker.exit(true);
        if (value){
            Checker newChecker = new Checker(this, crvProfile.getCrvModel().getId());
            newChecker.start();
            checkerList.put(crvProfile.getCrvModel().getId(), newChecker);
        } else {
            checkerList.remove(crvProfile.getCrvModel().getId());
        }

        CrvProfile.updateUser(crvRepository, crvProfile);
        sendMsg(crvProfile.getCrvModel().getId(), "Changed to " + value);
    }

    private void initCheckerList(){
        for (CrvProfile crvProfile : CrvProfile.getCrvProfiles(crvRepository)){
            if (!crvProfile.getCrvModel().getAuto()) continue;
            Checker checker = new Checker(this, crvProfile.getCrvModel().getId());
            checker.start();
            checkerList.put(crvProfile.getCrvModel().getId(), checker);
        }
    }
}
