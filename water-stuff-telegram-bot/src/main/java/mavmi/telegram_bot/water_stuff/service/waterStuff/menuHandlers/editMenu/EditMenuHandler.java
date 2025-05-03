package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.editMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class EditMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final MenuEngine menuEngine;
    private final CommonUtils commonUtils;

    public EditMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils) {
        super(menuEngine, WaterStuffServiceMenu.EDIT);
        this.commonUtils = commonUtils;
        this.menuEngine = menuEngine;
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return;
        }

        String msg = messageJson.getTextMessage();

        if (msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getEdit())) {
            onEdit(request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getEditGroup().getChangeName())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.EDIT_NAME, request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getEditGroup().getChangeDiff())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.EDIT_DIFF, request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getEditGroup().getChangeWater())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.EDIT_WATER, request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getManageGroup().getEditGroup().getChangeFertilize())) {
            menuEngine.proxyRequest(WaterStuffServiceMenu.EDIT_FERTILIZE, request);
        } else if (msg.equals(commonUtils.getConstants().getButtons().getCommon().getExit())) {
            exit(request);
        } else {
            commonUtils.error(request);
        }
    }

    private void onEdit(WaterStuffServiceRq request) {
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT);
        commonUtils.sendReplyKeyboard(request.getChatId(), commonUtils.getConstants().getPhrases().getManageGroup().getEditGroup(), commonUtils.getEditMenuButtons());
    }

    private void exit(WaterStuffServiceRq request) {
        commonUtils.dropUserMenu(WaterStuffServiceMenu.MANAGE_GROUP);
        commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMessagesContainer().clearMessages();
        commonUtils.sendReplyKeyboard(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getSuccess(), commonUtils.getManageMenuButtons());
    }
}
