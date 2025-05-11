package mavmi.telegram_bot.monitoring.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThreads;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.monitoring.MonitoringService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonitoringUserThreads extends UserThreads<MonitoringUserThread> {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final MonitoringService service;
    private final String hostTarget;

    public MonitoringUserThreads(@Value("${telegram-bot.task-target}") String hostTarget,
                                 @Value("${user-threads.max-count}") long maxThreadsCount,
                                 UserCachesProvider userCachesProvider,
                                 RequestsMapper requestsMapper,
                                 MonitoringService service) {
        super(maxThreadsCount);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.service = service;
        this.hostTarget = hostTarget;
    }

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        MonitoringUserThread userThread = (MonitoringUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new MonitoringUserThread(this, userCachesProvider, requestsMapper, service, hostTarget, chatId);
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
