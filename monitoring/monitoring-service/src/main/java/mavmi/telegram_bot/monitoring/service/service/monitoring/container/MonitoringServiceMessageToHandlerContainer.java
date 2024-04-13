package mavmi.telegram_bot.monitoring.service.service.monitoring.container;

import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.container.api.MessageToHandlerContainer;
import mavmi.telegram_bot.common.service.method.ServiceMethod;

import java.util.Map;

public class MonitoringServiceMessageToHandlerContainer extends MessageToHandlerContainer<MonitoringServiceRs, MonitoringServiceRq> {

    public MonitoringServiceMessageToHandlerContainer(Map<String, ServiceMethod<MonitoringServiceRs, MonitoringServiceRq>> requestToMethod, ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> defaultMethod) {
        super(requestToMethod, defaultMethod);
    }

    public MonitoringServiceMessageToHandlerContainer(ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> defaultMethod) {
        super(defaultMethod);
    }
}
