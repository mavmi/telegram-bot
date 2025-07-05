package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.rmMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.service.WaterDataService;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Component
public class RmMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public RmMenuHandler(MenuEngine menuEngine,
                         CommonUtils commonUtils,
                         TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.RM);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return;
        }

        String msg = messageJson.getTextMessage();

        if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "rm").getValue())) {
            approve(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.APPROVE, "yes").getValue())) {
            processYes(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.APPROVE, "no").getValue())) {
            processNo(request);
        }
    }

    private void approve(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuHistoryContainer().add(WaterStuffServiceMenu.RM);
        menuEngine.proxyRequest(WaterStuffServiceMenu.APPROVE, request);
    }

    private void processYes(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        WaterDataService waterDataService = commonUtils.getWaterDataService();

        waterDataService.remove(dataCache.getUserId(), dataCache.getSelectedGroup());
        dataCache.getMessagesContainer().clear();
        commonUtils.dropUserMenu(WaterStuffServiceMenu.MAIN_MENU);

        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getSuccess());
    }

    private void processNo(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
