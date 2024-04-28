package mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.constantsHandler.MonitoringServiceConstantsHandler;
import mavmi.telegram_bot.monitoring.service.constantsHandler.dto.MonitoringServiceConstants;
import mavmi.telegram_bot.monitoring.service.service.monitoring.container.MonitoringServiceMessageToHandlerContainer;
import mavmi.telegram_bot.monitoring.service.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToHandlerContainer monitoringServiceMessageToHandlerContainer;
    private final CommonServiceModule commonServiceModule;
    private final MonitoringServiceConstants constants;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            MonitoringServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToHandlerContainer(
                Map.of(
                        constants.getRequests().getApps(), this::apps,
                        constants.getRequests().getHost(), this::host
                ),
                commonServiceModule::error
        );
    }

    @Override
    public MonitoringServiceRs process(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> method = monitoringServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private MonitoringServiceRs apps(MonitoringServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(MonitoringServiceMenu.APPS);
        return commonServiceModule.createSendReplyKeyboardResponse(
                constants.getPhrases().getAvailableOptions(),
                commonServiceModule.getAppsButtons()
        );
    }

    private MonitoringServiceRs host(MonitoringServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(MonitoringServiceMenu.HOST);
        return commonServiceModule.createSendReplyKeyboardResponse(
                constants.getPhrases().getAvailableOptions(),
                commonServiceModule.getHostButtons()
        );
    }
}
