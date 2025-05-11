package mavmi.telegram_bot.lib.telegram_bot_starter.userThread;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@RequiredArgsConstructor
public abstract class UserThreads<T extends UserThread> {

    protected final ConcurrentMap<Long, T> tgIdToUserThread = new ConcurrentHashMap<>();
    protected final LinkedBlockingQueue<T> userThreadsQueue = new LinkedBlockingQueue<>();
    protected final long maxThreadsCount;

    public abstract void add(Update update);

    public void removeThread(long chatId) {
        synchronized (this) {
            tgIdToUserThread.remove(chatId);
            if (tgIdToUserThread.size() < maxThreadsCount) {
                if (userThreadsQueue.size() > 0) {
                    T userThread = userThreadsQueue.remove();
                    tgIdToUserThread.put(userThread.getChatId(), userThread);
                    Thread.ofVirtual().start(userThread);
                    log.info("Moved user thread from queue to pool. Queue size: {}; pool size: {}",
                            userThreadsQueue.size(),
                            tgIdToUserThread.size());
                } else {
                    log.info("Queue is empty. Pool size: {}", tgIdToUserThread.size());
                }
            }
        }
    }
}
