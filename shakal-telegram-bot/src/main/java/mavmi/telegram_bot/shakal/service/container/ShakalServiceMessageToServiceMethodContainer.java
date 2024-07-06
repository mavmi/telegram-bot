package mavmi.telegram_bot.shakal.service.container;

import mavmi.telegram_bot.common.service.container.api.MessageToServiceMethodContainer;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;

import java.util.Map;

public class ShakalServiceMessageToServiceMethodContainer extends MessageToServiceMethodContainer<ShakalServiceRs, ShakalServiceRq> {

    public ShakalServiceMessageToServiceMethodContainer(
            Map<String, ServiceMethod<ShakalServiceRs, ShakalServiceRq>> requestToMethod,
            ServiceMethod<ShakalServiceRs, ShakalServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public ShakalServiceMessageToServiceMethodContainer(Map<String, ServiceMethod<ShakalServiceRs, ShakalServiceRq>> requestToMethod) {
        super(requestToMethod);
    }

    public ShakalServiceMessageToServiceMethodContainer(ServiceMethod<ShakalServiceRs, ShakalServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
