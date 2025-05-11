package mavmi.telegram_bot.monitoring.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThread;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.monitoring.MonitoringService;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;

@Slf4j
public class MonitoringUserThread extends UserThread {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final MonitoringService monitoringService;
    private final String hostTarget;

    public MonitoringUserThread(MonitoringUserThreads userThreads,
                                UserCachesProvider userCachesProvider,
                                RequestsMapper requestsMapper,
                                MonitoringService monitoringService,
                                String hostTarget,
                                long chatId) {
        super(userThreads, chatId);
        this.userCachesProvider = userCachesProvider;
        this.requestsMapper = requestsMapper;
        this.monitoringService = monitoringService;
        this.hostTarget = hostTarget;
    }

    @Override
    public void run() {
        try {
            while (!updateQueue.isEmpty()) {
                Message message = updateQueue.remove().message();
                log.info("Got request from id {}", message.from().id());

                String msg = message.text();
                if (msg == null) {
                    log.info("Message is null");
                    continue;
                }

                MonitoringServiceRq monitoringServiceRq = requestsMapper.telegramMessageToMonitoringServiceRequest(message, hostTarget);
                monitoringService.handleRequest(monitoringServiceRq);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            userThreads.removeThread(chatId);
            userCachesProvider.clean();
        }
    }
}
