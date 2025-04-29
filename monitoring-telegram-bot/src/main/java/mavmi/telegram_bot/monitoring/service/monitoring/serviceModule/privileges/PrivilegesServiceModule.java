package mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.privileges;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.privilege.aop.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrivilegesServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @Autowired
    public void setup(PrivilegesInfoServiceModule privilegesInfoServiceModule,
                      PrivilegesAddServiceModule privilegesAddServiceModule,
                      PrivilegesDeleteServiceModule privilegesDeleteServiceModule) {
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getInfo(), privilegesInfoServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getAddPrivilege(), privilegesAddServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getPrivileges().getDeletePrivilege(), privilegesDeleteServiceModule::handleRequest)
                .setDefaultServiceMethod(commonServiceModule::error);
    }

    @VerifyPrivilege(PRIVILEGE.PRIVILEGES)
    public void initMenuLevel(MonitoringServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.PRIVILEGES);
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
