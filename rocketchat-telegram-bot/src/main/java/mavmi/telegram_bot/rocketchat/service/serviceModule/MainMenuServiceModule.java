package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final RocketchatServiceConstants constants;
    private final RocketchatServiceMessageToServiceMethodContainer rocketchatServiceMessageToServiceMethodContainer;

    public MainMenuServiceModule(
            AuthServiceModule authServiceModule,
            ExitServiceModule exitServiceModule,
            QrServiceModule qrServiceModule,
            CommonServiceModule commonServiceModule,
            RocketchatServiceConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.rocketchatServiceMessageToServiceMethodContainer = new RocketchatServiceMessageToServiceMethodContainer(
                Map.of(
                        constants.getRequests().getStart(), authServiceModule::handleRequest,
                        constants.getRequests().getAuth(), authServiceModule::handleRequest,
                        constants.getRequests().getExit(), exitServiceModule::handleRequest,
                        constants.getRequests().getQr(), qrServiceModule::handleRequest
                ),
                commonServiceModule::error
        );
    }

    @Override
    public RocketchatServiceRs handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return commonServiceModule.createBadRequestResponse();
        }

        String msg = messageJson.getTextMessage();
        ServiceMethod<RocketchatServiceRs, RocketchatServiceRq> method = rocketchatServiceMessageToServiceMethodContainer.getMethod(msg);
        return method.process(request);
    }
}
