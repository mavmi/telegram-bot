package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.appsMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class AppsMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;

    public AppsMenuHandler(MenuEngine menuEngine,
                           CommonServiceModule commonServiceModule) {
        super(menuEngine, MonitoringServiceMenu.APPS);
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getApps().getApps())) {
            init(request);
        } else if (msg.equals(commonServiceModule.getConstants().getButtons().getCommon().getExit())) {
            commonServiceModule.exit(request);
        } else {
            commonServiceModule.postTask(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).getMenuHistoryContainer().add(MonitoringServiceMenu.APPS);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
    }
}
