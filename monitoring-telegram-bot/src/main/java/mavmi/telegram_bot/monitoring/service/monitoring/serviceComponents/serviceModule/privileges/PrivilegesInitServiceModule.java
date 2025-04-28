package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges;

import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.database_starter.model.PrivilegesModel;
import mavmi.telegram_bot.lib.database_starter.repository.PrivilegesRepository;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.monitoring.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class PrivilegesInitServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final PrivilegesServiceModule privilegesServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public PrivilegesInitServiceModule(
            CommonServiceModule commonServiceModule,
            PrivilegesServiceModule privilegesServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.privilegesServiceModule = privilegesServiceModule;
        serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getPrivileges().getPrivileges(), this::init)
                .setDefaultServiceMethod(this::onDefault);
    }

    @Override
    @VerifyPrivilege(PRIVILEGE.PRIVILEGES)
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void onDefault(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        long chatIdToInspect = Utils.parseTelegramId(request.getMessageJson().getTextMessage());
        if (chatIdToInspect == -1) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getPrivileges().getInvalidId());
        } else {
            MonitoringDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class);
            PrivilegesRepository privilegesRepository = commonServiceModule.getPrivilegesRepository();
            Optional<PrivilegesModel> optional = privilegesRepository.findById(chatIdToInspect);
            dataCache.getPrivilegesManagement()
                    .setWorkingTelegramId(chatIdToInspect)
                    .setWorkingPrivileges((optional.isEmpty()) ? new ArrayList<>() : optional.get().getPrivileges());
            privilegesServiceModule.initMenuLevel(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.PRIVILEGES_INIT);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId(), commonServiceModule.getConstants().getPhrases().getPrivileges().getAskForUserId());
    }
}
