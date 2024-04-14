package mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.constantsHandler.MonitoringServiceConstantsHandler;
import mavmi.telegram_bot.monitoring.service.service.monitoring.container.MonitoringServiceMessageToHandlerContainer;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HostServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToHandlerContainer monitoringServiceMessageToHandlerContainer;

    public HostServiceModule(
            CommonServiceModule commonServiceModule,
            MonitoringServiceConstantsHandler constantsHandler
    ) {
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToHandlerContainer(
                Map.of(constantsHandler.get().getButtons().getExit(), commonServiceModule::exit),
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
