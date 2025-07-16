package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.editNameMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.service.WaterDataService;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Component
public class EditNameMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public EditNameMenuHandler(MenuEngine menuEngine,
                               CommonUtils commonUtils,
                               TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.EDIT_NAME);
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
        if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.EDIT, "change_name").getValue())) {
            onChangeName(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getCancel())) {
            cancel(request);
        } else {
            changeName(request);
        }
    }

    private void onChangeName(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuHistoryContainer().add(WaterStuffServiceMenu.EDIT_NAME);
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getEnterGroupName());
    }

    private void changeName(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        WaterDataService waterDataService = commonUtils.getWaterDataService();
        WaterStuffDto dto = waterDataService.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        String newGroupName = request.getMessageJson().getTextMessage();

        dataCache.setSelectedGroup(newGroupName);
        dto.setName(newGroupName);

        waterDataService.put(dto);
        dataCache.getMessagesContainer().clear();
        commonUtils.dropUserMenu();

        telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                commonUtils.getConstants().getPhrases().getCommon().getSuccess(),
                commonUtils.getMenuButtons(WaterStuffServiceMenu.EDIT, request.getChatId()));
    }

    private void cancel(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
