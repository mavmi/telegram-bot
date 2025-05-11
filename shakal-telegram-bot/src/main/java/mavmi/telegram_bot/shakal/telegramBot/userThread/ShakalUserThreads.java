package mavmi.telegram_bot.shakal.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThreads;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShakalUserThreads extends UserThreads<ShakalUserThread> {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final ShakalService shakalService;

    public ShakalUserThreads(@Value("${user-threads.max-count}") long maxThreadsCount,
                             UserCachesProvider userCachesProvider,
                             RequestsMapper requestsMapper,
                             ShakalService shakalService) {
        super(maxThreadsCount);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.shakalService = shakalService;
    }

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        ShakalUserThread userThread = (ShakalUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new ShakalUserThread(this, userCachesProvider, requestsMapper, shakalService, chatId);
            userThread.add(update);

            synchronized (this) {
                if (tgIdToUserThread.size() < maxThreadsCount) {
                    tgIdToUserThread.put(chatId, userThread);
                    Thread.ofVirtual().start(userThread);
                    log.info("New thread. Current pool size is {}", tgIdToUserThread.size());
                } else {
                    userThreadsQueue.add(userThread);
                    log.info("New thread in queue. Queue size is {}", userThreadsQueue.size());
                }
            }
        } else {
            userThread.add(update);
        }
    }
}
