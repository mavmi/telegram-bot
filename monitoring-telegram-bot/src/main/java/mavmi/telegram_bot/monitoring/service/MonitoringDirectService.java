package mavmi.telegram_bot.monitoring.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.service.container.direct.impl.MenuToServiceModuleContainer;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.direct.DirectService;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.monitoring.cache.MonitoringServiceAuthCache;
import mavmi.telegram_bot.monitoring.cache.MonitoringServiceDataCache;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceModule.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceModule.HostServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MonitoringDirectService implements DirectService<MonitoringServiceRs, MonitoringServiceRq> {

    private final UserAuthentication userAuthentication;
    private final CommonServiceModule commonServiceModule;
    private final MenuToServiceModuleContainer<MonitoringServiceRs, MonitoringServiceRq> menuToServiceModuleContainer;

    public MonitoringDirectService(
            UserAuthentication userAuthentication,
            AppsServiceModule appsServiceModule,
            HostServiceModule hostServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.userAuthentication = userAuthentication;
        this.commonServiceModule = commonServiceModule;
        this.menuToServiceModuleContainer = new MenuToServiceModuleContainer<>(
                Map.of(
                        MonitoringServiceMenu.MAIN_MENU, mainMenuServiceModule,
                        MonitoringServiceMenu.APPS, appsServiceModule,
                        MonitoringServiceMenu.HOST, hostServiceModule
                )
        );
    }

    @SetupUserCaches
    @Secured
    @Override
    @SneakyThrows
    public MonitoringServiceRs handleRequest(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        String msg = request.getMessageJson().getTextMessage();

        MonitoringServiceDataCache userCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringServiceDataCache.class);
        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return commonServiceModule.error(request);
        }

        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );

        Menu userMenu = userCache.getMenuContainer().getLast();
        ServiceModule<MonitoringServiceRs, MonitoringServiceRq> module = menuToServiceModuleContainer.get(userMenu);
        return module.handleRequest(request);
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return new MonitoringServiceDataCache(chatId, MonitoringServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new MonitoringServiceAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.MONITORING_BOT));
    }

    public List<Long> getAvailableIdx() {
        return commonServiceModule.getAvailableIdx();
    }
}
