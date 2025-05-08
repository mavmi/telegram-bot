package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.pauseMenu;

import lombok.SneakyThrows;
import mavmi.telegram_bot.lib.dto.service.common.CallbackQueryJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CalendarUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Component
public class PauseMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final CalendarUtils calendarUtils;
    private final TelegramBotUtils telegramBotUtils;

    public PauseMenuHandler(MenuEngine menuEngine,
                            CommonUtils commonUtils,
                            TelegramBotUtils telegramBotUtils,
                            CalendarUtils calendarUtils) {
        super(menuEngine, WaterStuffServiceMenu.PAUSE);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
        this.calendarUtils = calendarUtils;
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = (messageJson == null) ? null : messageJson.getTextMessage();
        if (msg != null && msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getPause())) {
            getCurrentMonthCalendar(request);
        } else {
            onDefault(request);
        }
    }

    private void getCurrentMonthCalendar(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.PAUSE);
        telegramBotUtils.sendInlineKeyboard(request.getChatId(),
                calendarUtils.getMonthYear(),
                calendarUtils.getMonthKeyboard());
    }

    @SneakyThrows
    private void onDefault(WaterStuffServiceRq request) {
        WaterConstants constants = commonUtils.getConstants();
        CallbackQueryJson callbackQueryJson = request.getCallbackQueryJson();
        if (callbackQueryJson == null) {
            return;
        }

        Integer msgId = callbackQueryJson.getMessageId();
        String msg = callbackQueryJson.getData();

        if (calendarUtils.isDayFormat(msg)) {
            WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
            long pauseUntil = CalendarUtils.DD_MM_YY_DATE_FORMAT.parse(msg).getTime();

            if (pauseUntil <= System.currentTimeMillis()) {
                dataCache.getMessagesContainer().clearMessages();
                commonUtils.dropUserMenu();
                telegramBotUtils.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getManageGroup().getInvalidDate(), commonUtils.getManageMenuButtons());
            } else {
                UsersWaterData usersWaterData = commonUtils.getUsersWaterData();
                WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());

                waterInfo.setStopNotificationsUntil(pauseUntil);
                usersWaterData.saveToFile();

                dataCache.getMessagesContainer().clearMessages();
                commonUtils.dropUserMenu();
                telegramBotUtils.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getCommon().getSuccess(), commonUtils.getManageMenuButtons());
            }
        } else if (calendarUtils.isMonthFormat(msg)) {
            telegramBotUtils.updateInlineKeyboard(request.getChatId(),
                    msgId,
                    calendarUtils.getMonthYear(msg),
                    calendarUtils.getMonthKeyboard(msg));
        }
    }
}
