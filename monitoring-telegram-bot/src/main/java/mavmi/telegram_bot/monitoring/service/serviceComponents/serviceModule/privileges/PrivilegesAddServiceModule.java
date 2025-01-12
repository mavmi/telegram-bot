package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.privileges;

import mavmi.telegram_bot.common.database.model.PrivilegesModel;
import mavmi.telegram_bot.common.database.repository.PrivilegesRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.Privileges;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesAddServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public PrivilegesAddServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getAddPrivilege(), this::init)
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

        Privileges cachedPrivileges = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getPrivileges();
        PrivilegesRepository privilegesRepository = commonServiceModule.getPrivilegesRepository();
        if (!cachedPrivileges.getWorkingPrivileges().contains(privilege)) {
            cachedPrivileges.getWorkingPrivileges().add(privilege);
            PrivilegesModel model = PrivilegesModel.builder()
                    .id(cachedPrivileges.getWorkingTelegramId())
                    .privileges(cachedPrivileges.getWorkingPrivileges())
                    .build();
            privilegesRepository.save(model);
        }

        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getMenuContainer().add(MonitoringServiceMenu.PRIVILEGES_ADD);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId(), commonServiceModule.getConstants().getPhrases().getPrivileges().getSelectPrivilege());
    }
}
