package mavmi.telegram_bot.monitoring.service.serviceModule;

import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.monitoring.cache.MonitoringServiceDataCache;
import mavmi.telegram_bot.monitoring.service.container.MonitoringServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToServiceMethodContainer monitoringServiceMessageToHandlerContainer;
    private final CommonServiceModule commonServiceModule;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getApps(), this::apps,
                        commonServiceModule.getConstants().getRequests().getHost(), this::host
                ),
                commonServiceModule::error
        );
    }

    @Override
    public MonitoringServiceRs handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> method = monitoringServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private MonitoringServiceRs apps(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringServiceDataCache.class).getMenuContainer().add(MonitoringServiceMenu.APPS);
        return commonServiceModule.createSendReplyKeyboardResponse(
                commonServiceModule.getConstants().getPhrases().getAvailableOptions(),
                commonServiceModule.getAppsButtons()
        );
    }

    private MonitoringServiceRs host(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringServiceDataCache.class).getMenuContainer().add(MonitoringServiceMenu.HOST);
        return commonServiceModule.createSendReplyKeyboardResponse(
                commonServiceModule.getConstants().getPhrases().getAvailableOptions(),
                commonServiceModule.getHostButtons()
        );
    }
}
