package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.AuthServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.qr.QrServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainMenuServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @Autowired
    public void setup(AuthServiceModule authServiceModule,
                      ExitServiceModule exitServiceModule,
                      QrServiceModule qrServiceModule) {
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
