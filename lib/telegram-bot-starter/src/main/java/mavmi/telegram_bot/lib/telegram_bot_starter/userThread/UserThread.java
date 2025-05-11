package mavmi.telegram_bot.lib.telegram_bot_starter.userThread;

import com.pengrad.telegrambot.model.Update;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.LinkedBlockingQueue;

@Getter
@RequiredArgsConstructor
public abstract class UserThread implements Runnable {

    protected final LinkedBlockingQueue<Update> updateQueue = new LinkedBlockingQueue<>();
    protected final UserThreads userThreads;
    protected final long chatId;

    public void add(Update update) {
        updateQueue.add(update);
    }
}
