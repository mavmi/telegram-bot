package mavmi.telegram_bot.rocketchat.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThread;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketService;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;

@Slf4j
public class RocketUserThread extends UserThread {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final RocketService rocketService;

    public RocketUserThread(RocketUserThreads userThreads,
                            UserCachesProvider userCachesProvider,
                            RequestsMapper requestsMapper,
                            RocketService rocketService,
                            long chatId) {
        super(userThreads, chatId);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.rocketService = rocketService;
    }

    @Override
    @SneakyThrows
    public void run() {
        try {
            while (!updateQueue.isEmpty()) {
                try {
                    Message message = updateQueue.remove().message();
                    RocketchatServiceRq rocketchatServiceRq = requestsMapper.telegramRequestToRocketchatServiceRequest(message);
                    rocketService.handleRequest(rocketchatServiceRq);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            userThreads.removeThread(chatId);
            userCachesProvider.clean();
        }
    }
}
