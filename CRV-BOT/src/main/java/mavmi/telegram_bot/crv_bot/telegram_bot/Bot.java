package mavmi.telegram_bot.crv_bot.telegram_bot;

import com.google.gson.JsonParser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AccessLevel;
import lombok.Getter;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import mavmi.telegram_bot.crv_bot.user.User;
import mavmi.telegram_bot.common.logger.Logger;
import okhttp3.OkHttpClient;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.sql.Array;
import java.util.HashMap;
import java.util.Map;

import static mavmi.telegram_bot.crv_bot.constants.Requests.*;

@Getter(value = AccessLevel.PACKAGE)
public class Bot {
    private JdbcTemplate jdbcTemplate;
    private OkHttpClient okHttpClient;
    private Logger logger;
    private TelegramBot telegramBot;
    private RequestOptions requestOptions;

    private final Map<Long, Checker> checkerList = new HashMap<>();

    public Bot(){
        okHttpClient = new OkHttpClient();
    }

    public Bot setTelegramBot(String telegramBotToken){
        telegramBot = new TelegramBot(telegramBotToken);
        return this;
    }
    public Bot setLogger(Logger logger){
        this.logger = logger;
        return this;
    }
    public Bot setRequestOptions(RequestOptions requestOptions){
        this.requestOptions = requestOptions;
        return this;
    }
    public Bot setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        return this;
    }

    public void run(){
        if (!checkValidity()) throw new RuntimeException("Bot is not set up");

        logger.log("CRV-BOT IS RUNNING");
        initCheckerList();
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                long userId = update.message().chat().id();
                String clientMsg = update.message().text();

                User user = User.getUser(jdbcTemplate, userId);
                if (user == null) continue;
                logEvent(update.message());
                switch (clientMsg) {
                    case (GET_COUNT_REQUEST) -> checkCrvCount(user);
                    case (AUTO_REQUEST) -> auto(user);
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

    void checkCrvCount(User user){
        try {
            String response = okHttpClient.newCall(user.getCrvCountRequest(requestOptions)).execute().body().string();
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

            sendMsg(user.getId(), Integer.toString(i));
            Array array = user.getRedirect();
            if (array == null || i == 0) return;
            for (long idx : (Long[]) array.getArray()){
                sendMsg(idx, Integer.toString(i));
            }
        } catch (Exception e) {
            logger.err(e.getMessage());
            sendMsg(user.getId(), "BOT_ERROR");
        }
    }
    void auto(User user){
        boolean value = !user.getAuto();
        user.setAuto(value);

        Checker oldChecker = checkerList.get(user.getId());
        if (oldChecker != null) oldChecker.exit(true);
        if (value){
            Checker newChecker = new Checker(this, user.getId());
            newChecker.start();
            checkerList.put(user.getId(), newChecker);
        } else {
            checkerList.remove(user.getId());
        }

        User.updateUser(jdbcTemplate, user);
        sendMsg(user.getId(), "Changed to " + value);
    }

    private void initCheckerList(){
        for (User user : User.getUsers(jdbcTemplate)){
            if (!user.getAuto()) continue;
            Checker checker = new Checker(this, user.getId());
            checker.start();
            checkerList.put(user.getId(), checker);
        }
    }

    private void logEvent(Message message){
        com.pengrad.telegrambot.model.User user = message.from();
        logger.log(
                    "USER_ID: [" +
                    user.id() +
                    "], " +
                    "USERNAME: [" +
                    user.username() +
                    "], " +
                    "FIRST_NAME: [" +
                    user.firstName() +
                    "], " +
                    "LAST_NAME: [" +
                    user.lastName() +
                    "], " +
                    "MESSAGE: [" +
                    message.text() +
                    "]"
        );
    }
    private boolean checkValidity(){
        return telegramBot != null &&
                logger != null &&
                requestOptions != null &&
                jdbcTemplate != null;
    }
}
