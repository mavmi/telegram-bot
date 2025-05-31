package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.pmsElementMenu;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import org.springframework.stereotype.Component;

@Component
public class PmsElementMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    public PmsElementMenuHandler(MenuEngine menuEngine) {
        super(menuEngine, MonitoringServiceMenu.PMS_ELEMENT);
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {

    }
}
