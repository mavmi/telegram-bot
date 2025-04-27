package mavmi.telegram_bot.lib.telegram_bot_starter.userThread;

import com.pengrad.telegrambot.model.Update;

import java.util.HashMap;
import java.util.Map;

public abstract class UserThreads<T extends UserThread> {

    protected final Map<Long, T> tgIdToUserThread;

    public UserThreads() {
        this.tgIdToUserThread = new HashMap<>();
    }

    public abstract void add(Update update);

    public void removeThread(long chatId) {
        tgIdToUserThread.remove(chatId);
    }
}
