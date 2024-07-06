package mavmi.telegram_bot.water_stuff.service.water_stuff.container;

import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.container.api.MessageToServiceMethodContainer;
import mavmi.telegram_bot.common.service.method.ServiceMethod;

import java.util.Map;

public class WaterStuffServiceMessageToServiceMethodContainer extends MessageToServiceMethodContainer<WaterStuffServiceRs, WaterStuffServiceRq> {

    public WaterStuffServiceMessageToServiceMethodContainer(
            Map<String, ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq>> requestToMethod,
            ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public WaterStuffServiceMessageToServiceMethodContainer(Map<String, ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq>> requestToMethod) {
        super(requestToMethod);
    }

    public WaterStuffServiceMessageToServiceMethodContainer(ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
