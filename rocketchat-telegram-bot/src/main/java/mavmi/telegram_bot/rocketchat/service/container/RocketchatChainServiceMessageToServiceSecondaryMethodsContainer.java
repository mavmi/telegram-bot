package mavmi.telegram_bot.rocketchat.service.container;

import mavmi.telegram_bot.common.service.container.chained.api.MessageToChainServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;

import java.util.List;
import java.util.Map;

public class RocketchatChainServiceMessageToServiceSecondaryMethodsContainer extends MessageToChainServiceSecondaryMethodsContainer<RocketchatServiceRs, RocketchatServiceRq> {

    public RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(
            Map<String, List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>>> requestToMethods,
            List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> defaultMethods
    ) {
        super(requestToMethods, defaultMethods);
    }

    public RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(Map<String, List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>>> requestToMethods) {
        super(requestToMethods);
    }

    public RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> defaultMethods) {
        super(defaultMethods);
    }
}
