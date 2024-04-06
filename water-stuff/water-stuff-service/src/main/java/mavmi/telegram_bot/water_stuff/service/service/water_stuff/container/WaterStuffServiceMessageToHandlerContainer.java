package mavmi.telegram_bot.water_stuff.service.service.water_stuff.container;

import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.container.api.MessageToHandlerContainer;
import mavmi.telegram_bot.common.service.method.ServiceMethod;

import java.util.Map;

public class WaterStuffServiceMessageToHandlerContainer extends MessageToHandlerContainer<WaterStuffServiceRs, WaterStuffServiceRq> {

    public WaterStuffServiceMessageToHandlerContainer(
            Map<String, ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq>> requestToMethod,
            ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public WaterStuffServiceMessageToHandlerContainer(ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
