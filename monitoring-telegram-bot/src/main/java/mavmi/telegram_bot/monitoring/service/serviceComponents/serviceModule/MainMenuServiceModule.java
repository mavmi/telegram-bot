package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.apps.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.privileges.PrivilegesInitServiceModule;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.serverInfo.ServerInfoServiceModule;
import org.springframework.stereotype.Component;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();
    private final CommonServiceModule commonServiceModule;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            ServerInfoServiceModule serverInfoServiceModule,
            AppsServiceModule appsServiceModule,
            PrivilegesInitServiceModule privilegesInitServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;

        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getApps().getApps(), appsServiceModule::initMenuLevel)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getServerInfo().getServerInfo(), serverInfoServiceModule::initMenuLevel)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getPrivileges().getPrivileges(), privilegesInitServiceModule::initMenuLevel)
                .setDefaultServiceMethod(this::onDefault);
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void onDefault(MonitoringServiceRq request) {
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
    }
}
