package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.mainMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonUtils commonUtils;

    public MainMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils) {
        super(menuEngine, MonitoringServiceMenu.MAIN_MENU);
        this.commonUtils = commonUtils;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.MAIN_MENU, "apps").getValue())) {
            menuEngine.proxyRequest(MonitoringServiceMenu.APPS, request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.MAIN_MENU, "server_info").getValue())) {
            menuEngine.proxyRequest(MonitoringServiceMenu.HOST, request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.MAIN_MENU, "pms").getValue())) {
            menuEngine.proxyRequest(MonitoringServiceMenu.PMS_MAIN, request);
        } else {
            commonUtils.sendCurrentMenuButtons(request.getChatId());
        }
    }
}
