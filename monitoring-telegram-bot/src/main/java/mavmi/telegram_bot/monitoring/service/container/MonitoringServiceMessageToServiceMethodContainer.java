package mavmi.telegram_bot.monitoring.service.container;


import mavmi.telegram_bot.common.service.container.direct.api.MessageToServiceMethodContainer;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;

import java.util.Map;

public class MonitoringServiceMessageToServiceMethodContainer extends MessageToServiceMethodContainer<MonitoringServiceRs, MonitoringServiceRq> {

    public MonitoringServiceMessageToServiceMethodContainer(
            Map<String, ServiceMethod<MonitoringServiceRs, MonitoringServiceRq>> requestToMethod,
            ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> defaultMethod
    ) {
        super(requestToMethod, defaultMethod);
    }

    public MonitoringServiceMessageToServiceMethodContainer(Map<String, ServiceMethod<MonitoringServiceRs, MonitoringServiceRq>> requestToMethod) {
        super(requestToMethod);
    }

    public MonitoringServiceMessageToServiceMethodContainer(ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
