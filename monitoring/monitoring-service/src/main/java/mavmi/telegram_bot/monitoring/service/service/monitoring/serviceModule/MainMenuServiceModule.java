package mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.service.constants.Phrases;
import mavmi.telegram_bot.monitoring.service.constants.Requests;
import mavmi.telegram_bot.monitoring.service.service.monitoring.container.MonitoringServiceMessageToHandlerContainer;
import mavmi.telegram_bot.monitoring.service.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRs, MonitoringServiceRq> {

    private final MonitoringServiceMessageToHandlerContainer monitoringServiceMessageToHandlerContainer;
    private final CommonServiceModule commonServiceModule;

    public MainMenuServiceModule(CommonServiceModule commonServiceModule) {
        this.monitoringServiceMessageToHandlerContainer = new MonitoringServiceMessageToHandlerContainer(
                Map.of(
                        Requests.APPS_REQ, this::apps,
                        Requests.HOST_REQ, this::host
                ),
                commonServiceModule::error
        );
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public MonitoringServiceRs process(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRs, MonitoringServiceRq> method = monitoringServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private MonitoringServiceRs apps(MonitoringServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(MonitoringServiceMenu.APPS);
        return commonServiceModule.createSendKeyboardResponse(Phrases.AVAILABLE_OPTIONS_MSG, CommonServiceModule.APPS_BUTTONS);
    }

    private MonitoringServiceRs host(MonitoringServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(MonitoringServiceMenu.HOST);
        return commonServiceModule.createSendKeyboardResponse(Phrases.AVAILABLE_OPTIONS_MSG, CommonServiceModule.HOST_BUTTONS);
    }
}
