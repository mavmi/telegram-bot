package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.hostMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class HostMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonUtils commonUtils;

    public HostMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils) {
        super(menuEngine, MonitoringServiceMenu.HOST);
        this.commonUtils = commonUtils;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonUtils.getConstants().getButtons().getMainMenuOptions().getServerInfo().getServerInfo())) {
            init(request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getCommon().getExit())) {
            commonUtils.exit(request);
        } else {
            commonUtils.postTask(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonUtils.getUserCaches().getDataCache(MonitoringDataCache.class).getMenuHistoryContainer().add(MonitoringServiceMenu.HOST);
        commonUtils.sendCurrentMenuButtons(request.getChatId());
    }
}
