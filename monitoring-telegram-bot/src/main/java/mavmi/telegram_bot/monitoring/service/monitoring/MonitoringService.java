package mavmi.telegram_bot.monitoring.service.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Monitoring telegram bot service entrypoint
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringService implements Service<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final MenuEngine menuEngine;

    @SetupUserCaches
    @Secured
    @Override
    @SneakyThrows
    public void handleRequest(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        String msg = request.getMessageJson().getTextMessage();
        MonitoringDataCache userCache = commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class);

        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return;
        }

        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                request.getChatId(),
                request.getUserJson().getUsername(),
                request.getUserJson().getFirstName(),
                request.getUserJson().getLastName(),
                msg
        );

        Menu menu = userCache.getMenuHistoryContainer().getLast();
        menuEngine.proxyRequest(menu, request);
    }

    public List<Long> getAvailableIdx() {
        return commonServiceModule.getAvailableIdx();
    }
}
