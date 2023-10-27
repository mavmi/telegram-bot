package mavmi.telegram_bot.common.bot;

import com.pengrad.telegrambot.model.Message;
import lombok.Getter;
import mavmi.telegram_bot.common.logger.Logger;

@Getter
public abstract class AbsTelegramBot {
    protected final Logger logger;

    public AbsTelegramBot(Logger logger){
        this.logger = logger;
    }
    public abstract void run();

    protected void logEvent(Message message){
        com.pengrad.telegrambot.model.User user = message.from();
        logger.log(
                "USER_ID: [" +
                        user.id() +
                        "], " +
                        "USERNAME: [" +
                        user.username() +
                        "], " +
                        "FIRSTNAME: [" +
                        user.firstName() +
                        "], " +
                        "LASTNAME: [" +
                        user.lastName() +
                        "], " +
                        "MESSAGE: [" +
                        message.text() +
                        "]"
        );
    }
}
