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
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;

    public AddMenuHandler(MenuEngine menuEngine,
                          CommonUtils commonUtils) {
        super(menuEngine, WaterStuffServiceMenu.ADD);
        this.commonUtils = commonUtils;
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
        } else if (msg.equals(commonUtils.getConstants().getButtons().getCommon().getYes())) {
            processYes(request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getCommon().getNo())) {
            processNo(request);
        } else {
            commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMessagesContainer().addMessage(msg);
            menuEngine.proxyRequest(WaterStuffServiceMenu.APPROVE, request);
        }
    }

    private void askForData(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.ADD);
        commonUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getAdd());
    }

    private void processYes(WaterStuffServiceRq request) {
        WaterConstants constants = commonUtils.getConstants();
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonUtils.getUsersWaterData();

        try {
            String[] splitted = dataCache
                    .getMessagesContainer()
                    .getLastMessage()
                    .replaceAll(" ", "").split(";");
            if (splitted.length != 2) {
                throw new NumberFormatException();
            }
            dataCache.getMessagesContainer().removeLastMessage();

            String name = splitted[0];
            int diff = Integer.parseInt(splitted[1]);

            WaterInfo waterInfo = new WaterInfo();
            waterInfo.setUserId(dataCache.getUserId());
            waterInfo.setName(name);
            waterInfo.setDiff(diff);
            waterInfo.setWaterFromString(WaterInfo.NULL_STR);
            waterInfo.setFertilizeFromString(WaterInfo.NULL_STR);
            usersWaterData.put(dataCache.getUserId(), waterInfo);

            commonUtils.sendText(request.getChatId(), constants.getPhrases().getCommon().getSuccess());
        } catch (NumberFormatException | DataException e) {
            log.error(e.getMessage(), e);
            commonUtils.sendText(request.getChatId(), constants.getPhrases().getManageGroup().getInvalidGroupNameFormat());
        } finally {
            dataCache.getMessagesContainer().clearMessages();
            commonUtils.dropUserMenu();
        }
    }

    private void processNo(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
