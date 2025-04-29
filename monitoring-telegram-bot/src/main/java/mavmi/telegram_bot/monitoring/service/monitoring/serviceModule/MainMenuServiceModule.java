package mavmi.telegram_bot.monitoring.service.monitoring.serviceModule;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.apps.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.botAccess.BotAccessInitServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.certGeneration.CerificateGenerationServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.pms.PmsServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.privileges.PrivilegesInitServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.serverInfo.ServerInfoServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();
    private final CommonServiceModule commonServiceModule;

    @Autowired
    public void setup(ServerInfoServiceModule serverInfoServiceModule,
                      AppsServiceModule appsServiceModule,
                      PrivilegesInitServiceModule privilegesInitServiceModule,
                      PmsServiceModule pmsServiceModule,
                      BotAccessInitServiceModule botAccessInitServiceModule,
                      CerificateGenerationServiceModule cerificateGenerationServiceModule) {
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getApps().getApps(), appsServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getServerInfo().getServerInfo(), serverInfoServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getPrivileges().getPrivileges(), privilegesInitServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getPms().getPms(), pmsServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getBotAccess().getBotAccess(), botAccessInitServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getCertGeneration().getCertGeneration(), cerificateGenerationServiceModule::handleRequest)
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
