package mavmi.telegram_bot.common.service;

import com.pengrad.telegrambot.model.Message;
import lombok.Setter;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsService {
    @Setter
    protected AbsTelegramBot telegramBot;
    protected Map<Long, AbsServiceUser> idToUser;

    public AbsService() {
        this.idToUser = new HashMap<>();
    }

    public abstract void handleRequest(Message telegramMessage);
}
