package mavmi.telegram_bot.shakal.service.service.shakal.container;

import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.container.api.MessageToHandlerContainer;
import mavmi.telegram_bot.common.service.method.ServiceMethod;

import java.util.Map;

public class ShakalServiceMessageToHandlerContainer extends MessageToHandlerContainer<ShakalServiceRs, ShakalServiceRq> {

    public ShakalServiceMessageToHandlerContainer(
            Map<String, ServiceMethod<ShakalServiceRs, ShakalServiceRq>> requestToMethod,
            ServiceMethod<ShakalServiceRs, ShakalServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public ShakalServiceMessageToHandlerContainer(Map<String, ServiceMethod<ShakalServiceRs, ShakalServiceRq>> requestToMethod) {
        super(requestToMethod);
    }

    public ShakalServiceMessageToHandlerContainer(ServiceMethod<ShakalServiceRs, ShakalServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
