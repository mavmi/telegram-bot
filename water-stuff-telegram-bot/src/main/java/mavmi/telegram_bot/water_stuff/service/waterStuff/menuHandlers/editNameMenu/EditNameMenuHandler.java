package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.editNameMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
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
        if (msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getEditGroup().getChangeName())) {
            onChangeName(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getCancel())) {
            cancel(request);
        } else {
            changeName(request);
        }
    }

    private void onChangeName(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_NAME);
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getEnterGroupName());
    }

    private void changeName(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonUtils.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        String newGroupName = request.getMessageJson().getTextMessage();

        dataCache.setSelectedGroup(newGroupName);
        waterInfo.setName(newGroupName);

        usersWaterData.saveToFile();
        dataCache.getMessagesContainer().clearMessages();
        commonUtils.dropUserMenu();

        telegramBotUtils.sendReplyKeyboard(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getSuccess(), commonUtils.getEditMenuButtons());
    }

    private void cancel(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
