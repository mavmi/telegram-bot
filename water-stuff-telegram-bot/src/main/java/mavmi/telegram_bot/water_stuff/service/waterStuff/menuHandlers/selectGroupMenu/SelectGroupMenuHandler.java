package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.selectGroupMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SelectGroupMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public SelectGroupMenuHandler(MenuEngine menuEngine,
                                  CommonUtils commonUtils,
                                  TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.SELECT_GROUP);
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
        if (msg.equals(commonUtils.getConstants().getRequests().getGetGroup())) {
            askForGroupTitle(request);
        } else {
            onDefault(request);
        }
    }

    private void askForGroupTitle(WaterStuffServiceRq request) {
        WaterConstants constants = commonUtils.getConstants();
        WaterDataCache user = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);

        if (commonUtils.getWaterDataService().size(user.getUserId()) == 0) {
            telegramBotUtils.sendText(request.getChatId(), constants.getPhrases().getManageGroup().getOnEmpty());
        } else {
            user.getMenuHistoryContainer().add(WaterStuffServiceMenu.SELECT_GROUP);
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getManageGroup().getEnterGroupName(),
                    commonUtils.getGroupsNames());
        }
    }

    private void onDefault(WaterStuffServiceRq request) {
        WaterConstants constants = commonUtils.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);

        if (commonUtils.getWaterDataService().get(dataCache.getUserId(), msg) == null) {
            dataCache.getMessagesContainer().clear();
            commonUtils.dropUserMenu();
            telegramBotUtils.sendTextDeleteKeyboard(request.getChatId(), constants.getPhrases().getManageGroup().getInvalidGroupName());
        } else {
            dataCache.getMenuHistoryContainer().add(WaterStuffServiceMenu.MANAGE_GROUP);
            dataCache.setSelectedGroup(msg);
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getManageGroup().getManageGroup(),
                    getMenuButtons(request.getChatId()));
        }
    }

    private List<String> getMenuButtons(long userId) {
        String selectedGroup = commonUtils.getUserCaches()
                .getDataCache(WaterDataCache.class)
                .getSelectedGroup();
        WaterStuffDto dto = commonUtils.getWaterDataService().get(userId, selectedGroup);

        List<String> buttons = commonUtils.getMenuButtons(WaterStuffServiceMenu.MANAGE_GROUP, userId);
        Long pauseUntil = dto.getStopNotificationsUntil();

        if (pauseUntil == null || pauseUntil < System.currentTimeMillis()) {
            buttons.removeIf(str -> str.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "continue").getValue()));
        } else {
            buttons.removeIf(str -> str.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "pause").getValue()));
        }

        return buttons;
    }
}
