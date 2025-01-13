package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.AuthServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.qr.QrServiceModule;
import org.springframework.stereotype.Component;

@Component
public class MainMenuServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public MainMenuServiceModule(
            AuthServiceModule authServiceModule,
            ExitServiceModule exitServiceModule,
            QrServiceModule qrServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getStart(), authServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getAuth(), authServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getExit(), exitServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getQr(), qrServiceModule::handleRequest)
                .setDefaultServiceMethod(this::error);
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            badRequest(request);
            return;
        }

        String msg = messageJson.getTextMessage();
        serviceComponentsContainer.getMethod(msg).process(request);
    }

    private void error(RocketchatServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getUnknownCommand());
    }

    private void badRequest(RocketchatServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getInvalidRequest());
    }
}
