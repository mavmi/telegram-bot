package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExitServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;

    public ExitServiceModule(
        CommonServiceModule commonServiceModule
    ) {
        List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methodsOnDefault = List.of(this::onDefault);

        this.rocketchatChainServiceMessageToServiceSecondaryMethodsContainer = new RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(
                methodsOnDefault
        );
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServiceSecondaryMethodsContainer.getMethods(msg);
    }

    private RocketchatServiceRs onDefault(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        commonServiceModule.getRocketchatRepository().deleteByTelegramId(chatId);
        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getOk());
    }
}
