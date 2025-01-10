package mavmi.telegram_bot.monitoring.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.model.PrivilegesModel;
import mavmi.telegram_bot.common.database.repository.PrivilegesRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.Service;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.cache.MonitoringAuthCache;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.HostServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
            HostServiceModule hostServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(MonitoringServiceMenu.MAIN_MENU, mainMenuServiceModule)
                .add(MonitoringServiceMenu.APPS, appsServiceModule)
                .add(MonitoringServiceMenu.HOST, hostServiceModule);
    }

    @SetupUserCaches
    @Secured
    @Override
    @SneakyThrows
    public void handleRequest(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        String msg = request.getMessageJson().getTextMessage();
        MonitoringDataCache userCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class);
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

            Menu userMenu = userCache.getMenuContainer().getLast();
            ServiceModule<MonitoringServiceRq> module = serviceComponentsContainer.getModule(userMenu);
            module.handleRequest(request);
        }
    }

    @Override
    public DataCache initDataCache(long chatId) {
        PrivilegesRepository privilegesRepository = commonServiceModule.getPrivilegesRepository();
        Optional<PrivilegesModel> optional = privilegesRepository.findById(chatId);
        List<PRIVILEGE> privileges = (optional.isPresent()) ? optional.get().getPrivileges() : Collections.emptyList();

        return new MonitoringDataCache(chatId, MonitoringServiceMenu.MAIN_MENU, privileges);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new MonitoringAuthCache(commonServiceModule.getUserAuthentication().isPrivilegeGranted(chatId, BOT_NAME.MONITORING_BOT));
    }

    public List<Long> getAvailableIdx() {
        return commonServiceModule.getAvailableIdx();
    }
}
