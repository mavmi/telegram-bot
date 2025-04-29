package mavmi.telegram_bot.monitoring.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.apps.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.botAccess.BotAccessInitServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.botAccess.BotAccessServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.pms.PmsNewValueServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.pms.PmsServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges.PrivilegesAddServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges.PrivilegesDeleteServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges.PrivilegesInitServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges.PrivilegesServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.serverInfo.ServerInfoServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Monitoring telegram bot service entrypoint
 */
@Slf4j
@Component
public class MonitoringService implements Service<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public MonitoringService(
            AppsServiceModule appsServiceModule,
            ServerInfoServiceModule serverInfoServiceModule,
            PrivilegesInitServiceModule privilegesInitServiceModule,
            PrivilegesServiceModule privilegesServiceModule,
            PrivilegesAddServiceModule privilegesAddServiceModule,
            PrivilegesDeleteServiceModule privilegesDeleteServiceModule,
            PmsServiceModule pmsServiceModule,
            PmsNewValueServiceModule pmsNewValueServiceModule,
            BotAccessInitServiceModule botAccessInitServiceModule,
            BotAccessServiceModule botAccessServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(MonitoringServiceMenu.MAIN_MENU, mainMenuServiceModule)
                .add(MonitoringServiceMenu.APPS, appsServiceModule)
                .add(MonitoringServiceMenu.HOST, serverInfoServiceModule)
                .add(MonitoringServiceMenu.PRIVILEGES_INIT, privilegesInitServiceModule)
                .add(MonitoringServiceMenu.PRIVILEGES, privilegesServiceModule)
                .add(MonitoringServiceMenu.PRIVILEGES_ADD, privilegesAddServiceModule)
                .add(MonitoringServiceMenu.PRIVILEGES_DELETE, privilegesDeleteServiceModule)
                .add(MonitoringServiceMenu.PMS, pmsServiceModule)
                .add(MonitoringServiceMenu.PMS_EDIT, pmsNewValueServiceModule)
                .add(MonitoringServiceMenu.BOT_ACCESS_INIT, botAccessInitServiceModule)
                .add(MonitoringServiceMenu.BOT_ACCESS, botAccessServiceModule);
    }

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
        } else {
            log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                    request.getChatId(),
                    request.getUserJson().getUsername(),
                    request.getUserJson().getFirstName(),
                    request.getUserJson().getLastName(),
                    msg
            );

            Menu userMenu = userCache.getMenu();
            ServiceModule<MonitoringServiceRq> module = serviceComponentsContainer.getModule(userMenu);
            module.handleRequest(request);
        }
    }
    public List<Long> getAvailableIdx() {
        return commonServiceModule.getAvailableIdx();
    }
}
