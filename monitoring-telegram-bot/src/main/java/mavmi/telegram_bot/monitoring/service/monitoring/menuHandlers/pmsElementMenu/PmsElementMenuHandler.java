package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.pmsElementMenu;

import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class PmsElementMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonUtils commonUtils;

    public PmsElementMenuHandler(MenuEngine menuEngine,
                                 CommonUtils commonUtils) {
        super(menuEngine, MonitoringServiceMenu.PMS_ELEMENT);
        this.commonUtils = commonUtils;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.PMS_ELEMENT, "info").getValue())) {
            onInfo(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.PMS_ELEMENT, "edit").getValue())) {
            menuEngine.proxyRequest(MonitoringServiceMenu.PMS_EDIT, request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.PMS_ELEMENT, "go_back").getValue())) {
            commonUtils.exit(request);
        } else {
            init(request);
        }
    }

    private void onInfo(MonitoringServiceRq request) {
        Parameter parameter = commonUtils.getUserCaches()
                .getDataCache(MonitoringDataCache.class)
                .getSelectedParameter();

        String infoStr = new StringBuilder()
                .append("Name: ")
                .append(parameter.getName())
                .append("\n")
                .append("Value: ")
                .append(parameter.getValue())
                .append("\n")
                .append("Value type: ")
                .append(parameter.getType())
                .toString();

        commonUtils.sendCurrentMenuButtons(request.getChatId(), infoStr);
    }

    private void init(MonitoringServiceRq request) {
        if (commonUtils.getUserCaches().getDataCache().getMenuHistoryContainer().getLast() != MonitoringServiceMenu.PMS_ELEMENT) {
            commonUtils.getUserCaches().getDataCache(MonitoringDataCache.class).getMenuHistoryContainer().add(MonitoringServiceMenu.PMS_ELEMENT);
        }

        commonUtils.sendCurrentMenuButtons(request.getChatId());
    }
}
