package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.mainMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainMenuMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public MainMenuMenuHandler(MenuEngine menuEngine,
                               CommonUtils commonUtils,
                               TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.MAIN_MENU);
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
        if (msg.equals(commonUtils.getConstants().getRequests().getAdd())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.ADD, request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getGetGroup())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.SELECT_GROUP, request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getGetFullInfo())) {
            getFullInfo(request);
        } else {
            commonUtils.error(request);
        }
    }

    private void getFullInfo(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        List<WaterStuffDto> dtoList = commonUtils.getWaterDataService()
                .getAll(dataCache.getUserId())
                .stream()
                .sorted((model1, model2) -> model1.getName().compareTo(model2.getName()))
                .toList();

        if (dtoList.isEmpty()) {
            telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getOnEmpty());
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterStuffDto dto : dtoList) {
                builder.append(commonUtils.getReadableWaterInfo(dto)).append("\n\n");
            }

            telegramBotUtils.sendText(request.getChatId(), builder.toString());
        }
    }
}
