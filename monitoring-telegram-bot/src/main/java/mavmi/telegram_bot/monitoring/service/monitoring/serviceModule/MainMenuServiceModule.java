package mavmi.telegram_bot.monitoring.service.monitoring.serviceModule;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class MainMenuServiceModule extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;

    public MainMenuServiceModule(MenuEngine menuEngine,
                                 CommonServiceModule commonServiceModule) {
        super(menuEngine, MonitoringServiceMenu.MAIN_MENU);
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getApps().getApps())) {
            menuEngine.proxyRequest(MonitoringServiceMenu.APPS, request);
        } else if (msg.equals(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getServerInfo().getServerInfo())) {
            menuEngine.proxyRequest(MonitoringServiceMenu.HOST, request);
        } else {
            commonServiceModule.sendCurrentMenuButtons(request.getChatId());
        }
    }
}
