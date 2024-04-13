package mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.constants.Buttons;
import mavmi.telegram_bot.monitoring.service.service.monitoring.container.MonitoringServiceMessageToHandlerContainer;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HostServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToHandlerContainer monitoringServiceMessageToHandlerContainer;

    public HostServiceModule(CommonServiceModule commonServiceModule) {
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToHandlerContainer(
                Map.of(Buttons.EXIT_BTN, commonServiceModule::exit),
                commonServiceModule::postTask
        );
    }

    @Override
    public MonitoringServiceRs process(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> method = monitoringServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }
}
