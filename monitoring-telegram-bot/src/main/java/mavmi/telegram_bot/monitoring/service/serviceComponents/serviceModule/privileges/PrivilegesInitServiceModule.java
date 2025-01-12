package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.privileges;

import mavmi.telegram_bot.common.database.model.PrivilegesModel;
import mavmi.telegram_bot.common.database.repository.PrivilegesRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
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
        long chatIdToInspect = parseTelegramId(request.getMessageJson().getTextMessage());
        if (chatIdToInspect == -1) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getPrivileges().getInvalidId());
        } else {
            MonitoringDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class);
            PrivilegesRepository privilegesRepository = commonServiceModule.getPrivilegesRepository();
            Optional<PrivilegesModel> optional = privilegesRepository.findById(chatIdToInspect);
            dataCache.getPrivileges()
                    .setWorkingTelegramId(chatIdToInspect)
                    .setWorkingPrivileges((optional.isEmpty()) ? new ArrayList<>() : optional.get().getPrivileges());
            privilegesServiceModule.initMenuLevel(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getMenuContainer().add(MonitoringServiceMenu.PRIVILEGES_INIT);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId(), commonServiceModule.getConstants().getPhrases().getPrivileges().getAskForUserId());
    }

    private long parseTelegramId(String textMessage) {
        long errorValue = -1;

        try {
            long chatId = Long.parseLong(textMessage);
            if (chatId <= 0) {
                throw new Exception();
            }

            return chatId;
        } catch (Exception e) {
            return errorValue;
        }
    }
}
