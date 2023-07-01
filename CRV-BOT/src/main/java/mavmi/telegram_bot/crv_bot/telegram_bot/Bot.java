package mavmi.telegram_bot.crv_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import mavmi.telegram_bot.utils.logger.Logger;

public class Bot {
    private Users users;
    private Logger logger;
    private TelegramBot telegramBot;

    public Bot(){

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

    public void run(){
        if (!checkValidity()) throw new RuntimeException("Bot is not set up");

        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                long userId = update.message().chat().id();
                String clientMsg = update.message().text();

                User user = users.get(userId);
                if (user == null) continue;

            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    private void checkCount(){

    }

    private boolean checkValidity(){
        return telegramBot != null &&
                logger != null &&
                users != null;
    }
}
