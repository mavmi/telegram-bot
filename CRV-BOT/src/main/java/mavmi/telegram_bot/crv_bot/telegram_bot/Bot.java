package mavmi.telegram_bot.crv_bot.telegram_bot;

import com.google.gson.JsonParser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import mavmi.telegram_bot.crv_bot.user.User;
import mavmi.telegram_bot.crv_bot.user.Users;
import mavmi.telegram_bot.utils.logger.Logger;
import okhttp3.OkHttpClient;

import static mavmi.telegram_bot.crv_bot.constants.Requests.*;

public class Bot {
    private OkHttpClient okHttpClient;
    private Users users;
    private Logger logger;
    private TelegramBot telegramBot;
    private RequestOptions requestOptions;

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
    public Bot setUsers(Users users){
        this.users = users;
        return this;
    }
    public Bot setHttpData(RequestOptions requestOptions){
        this.requestOptions = requestOptions;
        return this;
    }

    public void run(){
        if (!checkValidity()) throw new RuntimeException("Bot is not set up");

        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                long userId = update.message().chat().id();
                String clientMsg = update.message().text();

                User user = users.get(userId);
                if (user == null) continue;
                logEvent(update.message());
                switch (clientMsg){
                    case (getCount):
                        checkCrvCount(user);
                        break;
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    private synchronized void sendMsg(long id, String msg){
        telegramBot.execute(new SendMessage(id, msg));
    }

    private void checkCrvCount(User user){
        try {
            String response = okHttpClient.newCall(user.getCrvCountRequest(requestOptions)).execute().body().string();
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
        } catch (Exception e) {
            logger.err(e.getMessage());
            sendMsg(user.getId(), "BOT_ERROR");
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
                users != null &&
                requestOptions != null;
    }
}
