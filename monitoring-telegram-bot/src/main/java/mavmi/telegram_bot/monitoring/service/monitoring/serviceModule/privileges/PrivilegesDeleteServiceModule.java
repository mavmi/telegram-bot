package mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.privileges;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.database_starter.model.PrivilegesModel;
import mavmi.telegram_bot.lib.database_starter.repository.PrivilegesRepository;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.privilege.aop.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.PrivilegesManagement;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrivilegesDeleteServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @PostConstruct
    public void setup() {
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getDeletePrivilege(), this::init)
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
        String textMessage = request.getMessageJson().getTextMessage();
        PRIVILEGE privilege = PRIVILEGE.findByName(textMessage);
        if (privilege == null) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getError());
            return;
        }

        PrivilegesManagement cachedPrivilegesManagement = commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).getPrivilegesManagement();
        if (cachedPrivilegesManagement.getWorkingPrivileges().remove(privilege)) {
            PrivilegesRepository privilegesRepository = commonServiceModule.getPrivilegesRepository();
            PrivilegesModel model = PrivilegesModel.builder()
                    .id(cachedPrivilegesManagement.getWorkingTelegramId())
                    .privileges(cachedPrivilegesManagement.getWorkingPrivileges())
                    .build();
            privilegesRepository.save(model);
        }

        commonServiceModule.sendCurrentMenuButtons(chatId);
    }

    public void init(MonitoringServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.PRIVILEGES_DELETE);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId(), commonServiceModule.getConstants().getPhrases().getPrivileges().getSelectPrivilege());
    }
}
