package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.privileges;

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

@Component
public class PrivilegesServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public PrivilegesServiceModule(
            CommonServiceModule commonServiceModule,
            PrivilegesInfoServiceModule privilegesInfoServiceModule,
            PrivilegesAddServiceModule privilegesAddServiceModule,
            PrivilegesDeleteServiceModule privilegesDeleteServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getInfo(), privilegesInfoServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getAddPrivilege(), privilegesAddServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getDeletePrivilege(), privilegesDeleteServiceModule::handleRequest)
                .setDefaultServiceMethod(commonServiceModule::error);
    }

    @VerifyPrivilege(PRIVILEGE.PRIVILEGES)
    public void initMenuLevel(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.PRIVILEGES);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
    }

    @Override
    @VerifyPrivilege(PRIVILEGE.PRIVILEGES)
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }
}
