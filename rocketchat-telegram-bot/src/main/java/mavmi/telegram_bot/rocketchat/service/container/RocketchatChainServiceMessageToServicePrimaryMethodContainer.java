package mavmi.telegram_bot.rocketchat.service.container;

import mavmi.telegram_bot.common.service.container.chained.api.MessageToChainServicePrimaryMethodContainer;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModulePrimaryMethod;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;

import java.util.Map;

public class RocketchatChainServiceMessageToServicePrimaryMethodContainer extends MessageToChainServicePrimaryMethodContainer<RocketchatServiceRs, RocketchatServiceRq> {

    public RocketchatChainServiceMessageToServicePrimaryMethodContainer(
            Map<String, ChainedServiceModulePrimaryMethod<RocketchatServiceRs, RocketchatServiceRq>> requestToMethod,
            ChainedServiceModulePrimaryMethod<RocketchatServiceRs, RocketchatServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public RocketchatChainServiceMessageToServicePrimaryMethodContainer(Map<String, ChainedServiceModulePrimaryMethod<RocketchatServiceRs, RocketchatServiceRq>> requestToMethod) {
        super(requestToMethod);
    }

    public RocketchatChainServiceMessageToServicePrimaryMethodContainer(ChainedServiceModulePrimaryMethod<RocketchatServiceRs, RocketchatServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
