package mavmi.telegram_bot.hb.service.container;

import mavmi.telegram_bot.common.service.container.direct.api.MessageToServiceMethodContainer;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;

import java.util.Map;

public class HbServiceMessageToServiceMethodContainer extends MessageToServiceMethodContainer<HbServiceResponse, HbServiceRequest> {

    public HbServiceMessageToServiceMethodContainer(
            Map<String, ServiceMethod<HbServiceResponse, HbServiceRequest>> requestToMethod,
            ServiceMethod<HbServiceResponse, HbServiceRequest> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public HbServiceMessageToServiceMethodContainer(Map<String, ServiceMethod<HbServiceResponse, HbServiceRequest>> requestToMethod) {
        super(requestToMethod);
    }

    public HbServiceMessageToServiceMethodContainer(ServiceMethod<HbServiceResponse, HbServiceRequest> defaultMethod) {
        super(defaultMethod);
    }
}
