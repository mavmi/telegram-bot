package mavmi.telegram_bot.monitoring.service.serviceModule;

import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringServiceConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringServiceConstants;
import mavmi.telegram_bot.monitoring.service.container.MonitoringServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.monitoring.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppsServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToServiceMethodContainer monitoringServiceMessageToHandlerContainer;

    public AppsServiceModule(
            CommonServiceModule commonServiceModule,
            MonitoringServiceConstantsHandler constantsHandler
    ) {
        MonitoringServiceConstants constants = constantsHandler.get();
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToServiceMethodContainer(
                Map.of(constants.getButtons().getExit(), commonServiceModule::exit),
                commonServiceModule::postTask
        );
    }

    @Override
    public MonitoringServiceRs handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> method = monitoringServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }
}
