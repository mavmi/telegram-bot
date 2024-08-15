package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServicePrimaryMethodContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MainMenuServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final RocketchatServiceConstants constants;
    private final RocketchatChainServiceMessageToServicePrimaryMethodContainer rocketchatChainServiceMessageToServicePrimaryMethodContainer;

    public MainMenuServiceModule(
            AuthServiceModule authServiceModule,
            ExitServiceModule exitServiceModule,
            QrServiceModule qrServiceModule,
            CommonServiceModule commonServiceModule,
            RocketchatServiceConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.rocketchatChainServiceMessageToServicePrimaryMethodContainer = new RocketchatChainServiceMessageToServicePrimaryMethodContainer(
                Map.of(
                        constants.getRequests().getStart(), authServiceModule::prepareMethodsChain,
                        constants.getRequests().getAuth(), authServiceModule::prepareMethodsChain,
                        constants.getRequests().getExit(), exitServiceModule::prepareMethodsChain,
                        constants.getRequests().getQr(), qrServiceModule::prepareMethodsChain
                ),
                this::error
        );
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return badRequest();
        }

        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServicePrimaryMethodContainer.getMethod(msg).prepareMethodsChain(request);
    }

    private List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> error(RocketchatServiceRq rocketchatServiceRq) {
        return List.of(commonServiceModule::error);
    }

    private List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> badRequest() {
        return List.of(this::createBadRequestResponse);
    }

    private RocketchatServiceRs createBadRequestResponse(RocketchatServiceRq request) {
        return commonServiceModule.createBadRequestResponse();
    }
}
