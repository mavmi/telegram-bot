package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.editDiffMenu;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EditDiffMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public EditDiffMenuHandler(MenuEngine menuEngine,
                               CommonUtils commonUtils,
                               TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.EDIT_DIFF);
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
        if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.EDIT, "change_diff").getValue())) {
            onChangeDiff(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getCancel())) {
            cancel(request);
        } else {
            changeDiff(request);
        }
    }

    private void onChangeDiff(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_DIFF);
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getEnterGroupDiff());
    }

    private void changeDiff(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonUtils.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        WaterConstants constants = commonUtils.getConstants();

        try {
            int newDiffValue = Integer.parseInt(request.getMessageJson().getTextMessage());
            waterInfo.setDiff(newDiffValue);
            usersWaterData.saveToFile();

            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getSuccess(),
                    menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.EDIT));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getError(),
                    menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.EDIT));
        } finally {
            dataCache.getMessagesContainer().clearMessages();
            commonUtils.dropUserMenu();
        }
    }

    private void cancel(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
