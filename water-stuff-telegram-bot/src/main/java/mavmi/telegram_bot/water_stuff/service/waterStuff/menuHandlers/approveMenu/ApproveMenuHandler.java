package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.approveMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import org.springframework.stereotype.Component;

@Component
public class ApproveMenuHandler extends MenuRequestHandler<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;

    public ApproveMenuHandler(MenuEngine menuEngine,
                              CommonUtils commonUtils) {
        super(menuEngine, WaterStuffServiceMenu.APPROVE);
        this.commonUtils = commonUtils;
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return;
        }

        approve(request);
    }

    private void approve(WaterStuffServiceRq request) {
        WaterConstants constants = commonUtils.getConstants();
        commonUtils.sendReplyKeyboard(
                request.getChatId(),
                constants.getPhrases().getCommon().getApprove(),
                new String[]{
                        constants.getButtons().getCommon().getYes(),
                        constants.getButtons().getCommon().getNo()
                }
        );
    }
}
