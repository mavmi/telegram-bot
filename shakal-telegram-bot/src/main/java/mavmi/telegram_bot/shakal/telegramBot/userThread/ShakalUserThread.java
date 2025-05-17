package mavmi.telegram_bot.shakal.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThread;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalService;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;

@Slf4j
public class ShakalUserThread extends UserThread {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final ShakalService shakalService;

    public ShakalUserThread(ShakalUserThreads userThreads,
                             UserCachesProvider userCachesProvider,
                             RequestsMapper requestsMapper,
                             ShakalService shakalService,
                             long chatId) {
        super(userThreads, chatId);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.shakalService = shakalService;
    }

    @Override
    public void run() {
        try {
            while (!updateQueue.isEmpty()) {
                Message message = updateQueue.remove().message();
                log.info("Got request from id {}", message.from().id());

                ShakalServiceRq shakalServiceRq = requestsMapper.telegramRequestToShakalServiceRequest(message);
                shakalService.handleRequest(shakalServiceRq);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            userThreads.removeThread(chatId);
            userCachesProvider.clean();
        }
    }
}
