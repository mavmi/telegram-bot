package mavmi.telegram_bot.rocketchat.service.menuHandlers.mainMenu;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.mainMenu.exit.ExitModule;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.mainMenu.qr.QrModule;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler extends MenuRequestHandler<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final QrModule qrModule;
    private final ExitModule exitModule;

    public MainMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils,
                           TelegramBotUtils telegramBotUtils,
                           QrModule qrModule,
                           ExitModule exitModule) {
        super(menuEngine, RocketMenu.MAIN_MENU);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
        this.qrModule = qrModule;
        this.exitModule = exitModule;
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            badRequest(request);
            return;
        }

        String msg = messageJson.getTextMessage();

        if (msg.equals(commonUtils.getConstants().getRequests().getStart())) {
            menuEngine.proxyRequest(RocketMenu.AUTH, request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getAuth())) {
            menuEngine.proxyRequest(RocketMenu.AUTH, request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getExit())) {
            exitModule.handleRequest(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getQr())) {
            qrModule.handleRequest(request);
        } else {
            error(request);
        }
    }

    private void error(RocketchatServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getUnknownCommand());
    }

    private void badRequest(RocketchatServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getInvalidRequest());
    }
}
