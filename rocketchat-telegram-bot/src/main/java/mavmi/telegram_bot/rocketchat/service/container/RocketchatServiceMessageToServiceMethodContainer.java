package mavmi.telegram_bot.rocketchat.service.container;

import mavmi.telegram_bot.common.service.container.api.MessageToServiceMethodContainer;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;

import java.util.Map;

public class RocketchatServiceMessageToServiceMethodContainer extends MessageToServiceMethodContainer<RocketchatServiceRs, RocketchatServiceRq>  {

    public RocketchatServiceMessageToServiceMethodContainer(
            Map<String, ServiceMethod<RocketchatServiceRs, RocketchatServiceRq>> requestToMethod,
            ServiceMethod<RocketchatServiceRs, RocketchatServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public RocketchatServiceMessageToServiceMethodContainer(Map<String, ServiceMethod<RocketchatServiceRs, RocketchatServiceRq>> requestToMethod) {
        super(requestToMethod);
    }

    public RocketchatServiceMessageToServiceMethodContainer(ServiceMethod<RocketchatServiceRs, RocketchatServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
