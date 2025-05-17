package mavmi.telegram_bot.water_stuff.telegramBot.userThread;

import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThreads;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.water_stuff.mapper.RequestsMapper;
import mavmi.telegram_bot.water_stuff.service.waterStuff.WaterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaterStuffUserThreads extends UserThreads<WaterStuffUserThread> {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final WaterService waterStuffService;

    public WaterStuffUserThreads(@Value("${user-threads.max-count}") long maxThreadsCount,
                                 UserCachesProvider userCachesProvider,
                                 RequestsMapper requestsMapper,
                                 WaterService waterStuffService) {
        super(maxThreadsCount);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.waterStuffService = waterStuffService;
    }

    @Override
    public void add(Update update) {
        long chatId;
        if (update.message() != null) {
            chatId = update.message().chat().id();
        } else {
            chatId = update.callbackQuery().from().id();
        }

        WaterStuffUserThread userThread = (WaterStuffUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new WaterStuffUserThread(this, userCachesProvider, requestsMapper, waterStuffService, chatId);
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
