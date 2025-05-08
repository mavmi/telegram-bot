package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.addMenu;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.exception.DataException;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public AddMenuHandler(MenuEngine menuEngine,
                          CommonUtils commonUtils,
                          TelegramBotUtils telegramBotUtils) {
        super(menuEngine, WaterStuffServiceMenu.ADD);
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
            askForData(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.APPROVE, "yes").getValue())) {
            processYes(request);
        } else if (msg.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.APPROVE, "no").getValue())) {
            processNo(request);
        } else {
            commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMessagesContainer().add(msg);
            menuEngine.proxyRequest(WaterStuffServiceMenu.APPROVE, request);
        }
    }

    private void askForData(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuHistoryContainer().add(WaterStuffServiceMenu.ADD);
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getAdd());
    }

    private void processYes(WaterStuffServiceRq request) {
        WaterConstants constants = commonUtils.getConstants();
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonUtils.getUsersWaterData();

        try {
            String[] splitted = dataCache
                    .getMessagesContainer()
                    .getLast()
                    .replaceAll(" ", "").split(";");
            if (splitted.length != 2) {
                throw new NumberFormatException();
            }
            dataCache.getMessagesContainer().getLastAndRemove();

            String name = splitted[0];
            int diff = Integer.parseInt(splitted[1]);

            WaterInfo waterInfo = new WaterInfo();
            waterInfo.setUserId(dataCache.getUserId());
            waterInfo.setName(name);
            waterInfo.setDiff(diff);
            waterInfo.setWaterFromString(WaterInfo.NULL_STR);
            waterInfo.setFertilizeFromString(WaterInfo.NULL_STR);
            usersWaterData.put(dataCache.getUserId(), waterInfo);

            telegramBotUtils.sendText(request.getChatId(), constants.getPhrases().getCommon().getSuccess());
        } catch (NumberFormatException | DataException e) {
            log.error(e.getMessage(), e);
            telegramBotUtils.sendText(request.getChatId(), constants.getPhrases().getManageGroup().getInvalidGroupNameFormat());
        } finally {
            dataCache.getMessagesContainer().clear();
            commonUtils.dropUserMenu();
        }
    }

    private void processNo(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
