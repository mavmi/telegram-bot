package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.pmsEditMenu;

import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Component
public class PmsEditMenuHandler extends MenuRequestHandler<MonitoringServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public PmsEditMenuHandler(MenuEngine menuEngine,
                              CommonUtils commonUtils,
                              TelegramBotUtils telegramBotUtils) {
        super(menuEngine, MonitoringServiceMenu.PMS_EDIT);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(MonitoringServiceRq request) {
        Menu menu = commonUtils.getUserCaches()
                .getDataCache(MonitoringDataCache.class)
                .getMenuHistoryContainer()
                .getLast();

        if (menu == MonitoringServiceMenu.PMS_ELEMENT) {
            init(request);
        } else if (menu == MonitoringServiceMenu.PMS_EDIT) {
            onEdit(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonUtils.getUserCaches()
                .getDataCache(MonitoringDataCache.class)
                .getMenuHistoryContainer()
                .add(MonitoringServiceMenu.PMS_EDIT);

        commonUtils.sendCurrentMenuButtons(request.getChatId(),
                commonUtils.getConstants().getPhrases().getPms().getEnterNewValue());
    }

    private void onEdit(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(menuEngine.getMenuButtonByName(MonitoringServiceMenu.PMS_EDIT, "go_back").getValue())) {
            commonUtils.exit(request);
        } else {
            RemoteParameterPlugin parameterPlugin = commonUtils.getRemoteParameterPlugin();
            Parameter parameter = commonUtils.getUserCaches()
                    .getDataCache(MonitoringDataCache.class)
                    .getSelectedParameter();

            parameter.setValue(request.getMessageJson().getTextMessage());
            if (parameterPlugin.updateParameter(parameter)) {
                telegramBotUtils.sendText(request.getChatId(),
                        commonUtils.getConstants().getPhrases().getCommon().getOk());
            } else {
                telegramBotUtils.sendText(request.getChatId(),
                        commonUtils.getConstants().getPhrases().getCommon().getError());
            }

            commonUtils.exit(request);
        }
    }
}
