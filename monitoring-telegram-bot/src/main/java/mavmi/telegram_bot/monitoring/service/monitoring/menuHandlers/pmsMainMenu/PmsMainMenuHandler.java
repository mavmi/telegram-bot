package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.pmsMainMenu;

import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.PmsUtils;
import org.springframework.stereotype.Component;

@Component
public class PmsMainMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonUtils commonUtils;
    private final PmsUtils pmsUtils;

    public PmsMainMenuHandler(MenuEngine menuEngine,
                              CommonUtils commonUtils,
                              PmsUtils pmsUtils) {
        super(menuEngine, MonitoringServiceMenu.PMS_MAIN);
        this.commonUtils = commonUtils;
        this.pmsUtils = pmsUtils;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.MAIN_MENU, "pms").getValue())) {
            init(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.PMS_MAIN, "go_back").getValue())) {
            commonUtils.exit(request);
        } else {
            onParameterSelect(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonUtils.getUserCaches().getDataCache(MonitoringDataCache.class).getMenuHistoryContainer().add(MonitoringServiceMenu.PMS_MAIN);
        commonUtils.sendCurrentMenuButtons(request.getChatId());
    }

    private void onParameterSelect(MonitoringServiceRq request) {
        String parameterName = request.getMessageJson().getTextMessage();
        Parameter parameter = pmsUtils.retrieveParameter(parameterName);

        if (parameter == null) {
            commonUtils.sendCurrentMenuButtons(request.getChatId(), commonUtils.getConstants().getPhrases().getPms().getInvalidParamName());
        } else {
            commonUtils.getUserCaches().getDataCache(MonitoringDataCache.class).getPmsCache().setSelectedParameter(parameter);
            menuEngine.proxyRequest(MonitoringServiceMenu.PMS_ELEMENT, request);
        }
    }
}
