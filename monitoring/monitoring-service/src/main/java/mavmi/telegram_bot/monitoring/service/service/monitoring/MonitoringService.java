package mavmi.telegram_bot.monitoring.service.service.monitoring;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.container.impl.MenuToServiceServiceModuleContainer;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.AbstractService;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.cache.MonitoringServiceUserDataCache;
import mavmi.telegram_bot.monitoring.service.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.HostServiceModule;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MonitoringService extends AbstractService {

    private final CommonServiceModule commonServiceModule;
    private final MenuToServiceServiceModuleContainer<MonitoringServiceRs, MonitoringServiceRq> menuToServiceServiceModuleContainer;

    public MonitoringService(
            AppsServiceModule appsServiceModule,
            HostServiceModule hostServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.menuToServiceServiceModuleContainer = new MenuToServiceServiceModuleContainer<>(
                Map.of(
                        MonitoringServiceMenu.MAIN_MENU, mainMenuServiceModule,
                        MonitoringServiceMenu.APPS, appsServiceModule,
                        MonitoringServiceMenu.HOST, hostServiceModule
                )
        );
    }

    @SneakyThrows
    public MonitoringServiceRs handleRequest(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        String msg = request.getMessageJson().getTextMessage();

        MonitoringServiceUserDataCache userCache = commonServiceModule.getUserSession().getCache();
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
        ServiceModule<MonitoringServiceRs, MonitoringServiceRq> module = menuToServiceServiceModuleContainer.get(userMenu);
        return module.process(request);
    }

    @Override
    public UserDataCache initCache() {
        return new MonitoringServiceUserDataCache(commonServiceModule.getUserSession().getId(), MonitoringServiceMenu.MAIN_MENU);
    }

    public List<Long> getAvailableIdx() {
        return commonServiceModule.getAvailableIdx();
    }
}
