package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.addMenu;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.exception.DataException;
import mavmi.telegram_bot.water_stuff.data.water.service.WaterDataService;
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
        WaterDataService waterDataService = commonUtils.getWaterDataService();

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
            long diff = Long.parseLong(splitted[1]);

            WaterModel waterModel = new WaterModel();
            waterModel.setUserId(dataCache.getUserId());
            waterModel.setName(name);
            waterModel.setDaysDiff(diff);
            waterModel.setWaterFromString(WaterModel.NULL_STR);
            waterModel.setFertilizeFromString(WaterModel.NULL_STR);
            waterDataService.put(waterModel);

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
