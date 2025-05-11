package mavmi.telegram_bot.lib.telegram_bot_starter.userThread;

import com.pengrad.telegrambot.model.Update;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class UserThreads<T extends UserThread> {

    protected final ConcurrentMap<Long, T> tgIdToUserThread = new ConcurrentHashMap<>();

    public abstract void add(Update update);

    public void removeThread(long chatId) {
        tgIdToUserThread.remove(chatId);
    }
}
