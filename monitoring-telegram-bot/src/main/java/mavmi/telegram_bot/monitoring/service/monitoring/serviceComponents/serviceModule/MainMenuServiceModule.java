package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.certs.CertificatesManagementService;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.apps.AppsServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.botAccess.BotAccessInitServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.certGeneration.CerificateGenerationServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.pms.PmsServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges.PrivilegesInitServiceModule;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.serverInfo.ServerInfoServiceModule;
import org.springframework.stereotype.Component;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();
    private final CommonServiceModule commonServiceModule;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            ServerInfoServiceModule serverInfoServiceModule,
            AppsServiceModule appsServiceModule,
            PrivilegesInitServiceModule privilegesInitServiceModule,
            PmsServiceModule pmsServiceModule,
            BotAccessInitServiceModule botAccessInitServiceModule,
            CerificateGenerationServiceModule cerificateGenerationServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;

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
