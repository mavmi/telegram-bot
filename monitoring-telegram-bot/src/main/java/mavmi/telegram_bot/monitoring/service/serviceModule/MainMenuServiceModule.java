package mavmi.telegram_bot.monitoring.service.serviceModule;

import mavmi.telegram_bot.monitoring.cache.MonitoringServiceDataCache;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringServiceConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringServiceConstants;
import mavmi.telegram_bot.monitoring.service.container.MonitoringServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToServiceMethodContainer monitoringServiceMessageToHandlerContainer;
    private final CommonServiceModule commonServiceModule;
    private final MonitoringServiceConstants constants;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            MonitoringServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToServiceMethodContainer(
                Map.of(
                        constants.getRequests().getApps(), this::apps,
                        constants.getRequests().getHost(), this::host
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
                constants.getPhrases().getAvailableOptions(),
                commonServiceModule.getAppsButtons()
        );
    }

    private MonitoringServiceRs host(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringServiceDataCache.class).getMenuContainer().add(MonitoringServiceMenu.HOST);
        return commonServiceModule.createSendReplyKeyboardResponse(
                constants.getPhrases().getAvailableOptions(),
                commonServiceModule.getHostButtons()
        );
    }
}
