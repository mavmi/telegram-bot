package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.manageGroupMenu;

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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Component
public class ManageGroupMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public ManageGroupMenuHandler(MenuEngine menuEngine,
                                  CommonUtils commonUtils,
                                  TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.MANAGE_GROUP);
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
        if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "edit").getValue())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.EDIT, request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "rm").getValue())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.RM, request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "info").getValue())) {
            getInfo(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "pause").getValue())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.PAUSE, request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "continue").getValue())) {
            continueNotifications(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "water").getValue())) {
            water(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "fertilize").getValue())) {
            fertilize(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "exit").getValue())) {
            exit(request);
        } else {
            commonUtils.error(request);
        }
    }

    private void getInfo(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        WaterInfo waterInfo = commonUtils.getUsersWaterData().get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Long stopNotificationsUntil = waterInfo.getStopNotificationsUntil();
        String res = commonUtils.getReadableWaterInfo(waterInfo);

        if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateTimeStr = simpleDateFormat.format(new java.util.Date(stopNotificationsUntil));
            res += "\n\n" +
                    "Обновления возобновятся " +
                    dateTimeStr;
        }

        telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                res,
                menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.MANAGE_GROUP));
    }

    private void continueNotifications(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonUtils.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());

        waterInfo.setStopNotificationsUntil(null);
        usersWaterData.saveToFile();
        telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                commonUtils.getConstants().getPhrases().getCommon().getSuccess(),
                menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.MANAGE_GROUP));
    }

    private void water(WaterStuffServiceRq request) {
        waterProcess(request.getChatId(), false);
    }

    private void fertilize(WaterStuffServiceRq request) {
        waterProcess(request.getChatId(), true);
    }

    private void exit(WaterStuffServiceRq request) {
        commonUtils.dropUserMenu(WaterStuffServiceMenu.MAIN_MENU);
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMessagesContainer().clearMessages();
        telegramBotUtils.sendTextDeleteKeyboard(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getSuccess());
    }

    private void waterProcess(long chatId, boolean fertilize) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonUtils.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());

        waterInfo.setWater(date);
        if (fertilize) {
            waterInfo.setFertilize(date);
        }
        usersWaterData.saveToFile();

        telegramBotUtils.sendReplyKeyboard(chatId,
                commonUtils.getConstants().getPhrases().getCommon().getSuccess(),
                menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.MANAGE_GROUP));
    }
}
