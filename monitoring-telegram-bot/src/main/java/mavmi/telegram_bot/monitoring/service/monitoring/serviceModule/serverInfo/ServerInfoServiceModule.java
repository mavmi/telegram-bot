package mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.serverInfo;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class ServerInfoServiceModule extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;

    public ServerInfoServiceModule(MenuEngine menuEngine,
                                   CommonServiceModule commonServiceModule) {
        super(menuEngine, MonitoringServiceMenu.HOST);
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getServerInfo().getServerInfo())) {
            init(request);
        } else if (msg.equals(commonServiceModule.getConstants().getButtons().getCommon().getExit())) {
            commonServiceModule.exit(request);
        } else {
            commonServiceModule.postTask(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.HOST);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
    }
}
