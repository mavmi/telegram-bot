package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class HostServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public HostServiceModule(CommonServiceModule commonServiceModule) {
        serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .setDefaultServiceMethod(commonServiceModule::postTask);
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }
}
