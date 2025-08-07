package mavmi.telegram_bot.rocketchat.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThread;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.monitoring.client.httpClient.MonitoringTelegramBotHttpClient;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.rocketchat.RocketService;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.utils.Utils;

@Slf4j
public class RocketUserThread extends UserThread {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final RocketService rocketService;
    private final MonitoringTelegramBotHttpClient monitoringTelegramBotHttpClient;

    public RocketUserThread(RocketUserThreads userThreads,
                            UserCachesProvider userCachesProvider,
                            RequestsMapper requestsMapper,
                            RocketService rocketService,
                            MonitoringTelegramBotHttpClient monitoringTelegramBotHttpClient,
                            long chatId) {
        super(userThreads, chatId);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.rocketService = rocketService;
        this.monitoringTelegramBotHttpClient = monitoringTelegramBotHttpClient;
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
                    monitoringTelegramBotHttpClient.notify(Utils.MONITORING_NOTIFY_NAME, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            monitoringTelegramBotHttpClient.notify(Utils.MONITORING_NOTIFY_NAME, e.getMessage());
        } finally {
            userThreads.removeThread(chatId);
            userCachesProvider.clean();
        }
    }
}
