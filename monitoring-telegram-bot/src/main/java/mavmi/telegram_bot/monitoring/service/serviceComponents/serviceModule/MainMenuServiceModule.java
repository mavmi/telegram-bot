package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class MainMenuServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();
    private final CommonServiceModule commonServiceModule;

    public MainMenuServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getApps(), this::apps)
                .add(commonServiceModule.getConstants().getRequests().getHost(), this::host)
                .setDefaultServiceMethod(commonServiceModule::error);
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void apps(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getMenuContainer().add(MonitoringServiceMenu.APPS);
        commonServiceModule.sendReplyKeyboard(
                request.getChatId(),
                commonServiceModule.getConstants().getPhrases().getCommon().getAvailableOptions(),
                commonServiceModule.getAppsButtons()
        );
    }

    private void host(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getMenuContainer().add(MonitoringServiceMenu.HOST);
        commonServiceModule.sendReplyKeyboard(
                request.getChatId(),
                commonServiceModule.getConstants().getPhrases().getCommon().getAvailableOptions(),
                commonServiceModule.getHostButtons()
        );
    }
}
