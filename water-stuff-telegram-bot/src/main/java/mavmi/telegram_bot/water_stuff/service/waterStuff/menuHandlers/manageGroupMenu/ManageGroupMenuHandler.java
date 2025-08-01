package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.manageGroupMenu;

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
        WaterStuffDto dto = commonUtils.getWaterDataService().get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Long stopNotificationsUntil = dto.getStopNotificationsUntil();
        String res = commonUtils.getReadableWaterInfo(dto);

        if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateTimeStr = simpleDateFormat.format(new java.util.Date(stopNotificationsUntil));
            res += "\n\n" +
                    "Обновления возобновятся " +
                    dateTimeStr;
        }

        telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                res,
                commonUtils.getMenuButtons(WaterStuffServiceMenu.MANAGE_GROUP, request.getChatId()));
    }

    private void continueNotifications(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        WaterDataService waterDataService = commonUtils.getWaterDataService();
        WaterStuffDto dto = waterDataService.get(dataCache.getUserId(), dataCache.getSelectedGroup());

        dto.setStopNotificationsUntil(null);
        waterDataService.put(dto);
        telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                commonUtils.getConstants().getPhrases().getCommon().getSuccess(),
                commonUtils.getMenuButtons(WaterStuffServiceMenu.MANAGE_GROUP, request.getChatId()));
    }

    private void water(WaterStuffServiceRq request) {
        waterProcess(request.getChatId(), false);
    }

    private void fertilize(WaterStuffServiceRq request) {
        waterProcess(request.getChatId(), true);
    }

    private void exit(WaterStuffServiceRq request) {
        commonUtils.dropUserMenu(WaterStuffServiceMenu.MAIN_MENU);
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMessagesContainer().clear();
        telegramBotUtils.sendTextDeleteKeyboard(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getSuccess());
    }

    private void waterProcess(long chatId, boolean fertilize) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        WaterDataService waterDataService = commonUtils.getWaterDataService();
        WaterStuffDto dto = waterDataService.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());

        dto.setWaterDate(date);
        if (fertilize) {
            dto.setFertilizeDate(date);
        }
        waterDataService.put(dto);

        telegramBotUtils.sendReplyKeyboard(chatId,
                commonUtils.getConstants().getPhrases().getCommon().getSuccess(),
                commonUtils.getMenuButtons(WaterStuffServiceMenu.MANAGE_GROUP, chatId));
    }
}
