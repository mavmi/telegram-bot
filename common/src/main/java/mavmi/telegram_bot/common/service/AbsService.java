package mavmi.telegram_bot.common.service;

import lombok.Setter;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsService {
    @Setter
    protected AbsTelegramBot telegramBot;
    protected Logger logger;
    protected Map<Long, AbsServiceUser> idToUser;

    public AbsService(Logger logger) {
        this.logger = logger;
        this.idToUser = new HashMap<>();
    }

    public abstract void handleRequest(Long chatId, String msg, String username, String firstName, String lastName);

    protected void logEvent(AbsServiceUser user, String msg) {
        logger.log(
                "USER_ID: [" +
                        user.getUserId() +
                        "], " +
                        "USERNAME: [" +
                        user.getUsername() +
                        "], " +
                        "FIRSTNAME: [" +
                        user.getFirstName() +
                        "], " +
                        "LASTNAME: [" +
                        user.getLastName() +
                        "], " +
                        "MESSAGE: [" +
                        msg +
                        "]"
        );
    }
}
