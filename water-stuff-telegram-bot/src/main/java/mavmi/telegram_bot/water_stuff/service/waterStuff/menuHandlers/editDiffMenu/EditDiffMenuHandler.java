package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.editDiffMenu;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.service.WaterDataService;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
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
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuHistoryContainer().add(WaterStuffServiceMenu.EDIT_DIFF);
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getEnterGroupDiff());
    }

    private void changeDiff(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        WaterDataService waterDataService = commonUtils.getWaterDataService();
        WaterStuffDto dto = waterDataService.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        WaterConstants constants = commonUtils.getConstants();

        try {
            long newDiffValue = Long.parseLong(request.getMessageJson().getTextMessage());
            dto.setDaysDiff(newDiffValue);
            waterDataService.put(dto);

            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getSuccess(),
                    commonUtils.getMenuButtons(WaterStuffServiceMenu.EDIT, request.getChatId()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getError(),
                    commonUtils.getMenuButtons(WaterStuffServiceMenu.EDIT, request.getChatId()));
        } finally {
            dataCache.getMessagesContainer().clear();
            commonUtils.dropUserMenu();
        }
    }

    private void cancel(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
