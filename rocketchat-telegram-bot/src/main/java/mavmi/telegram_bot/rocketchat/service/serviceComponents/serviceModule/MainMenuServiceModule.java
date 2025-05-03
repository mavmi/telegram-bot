package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class MainMenuServiceModule extends MenuRequestHandler<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final MenuEngine menuEngine;

    public MainMenuServiceModule(MenuEngine menuEngine,
                                 CommonServiceModule commonServiceModule) {
        super(menuEngine, RocketMenu.MAIN_MENU);
        this.menuEngine = menuEngine;
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            badRequest(request);
            return;
        }

        String msg = messageJson.getTextMessage();

        if (msg.equals(commonServiceModule.getConstants().getRequests().getStart())) {
            menuEngine.proxyRequest(RocketMenu.AUTH, request);
        } else if (msg.equals(commonServiceModule.getConstants().getRequests().getAuth())) {
            menuEngine.proxyRequest(RocketMenu.AUTH, request);
        } else if (msg.equals(commonServiceModule.getConstants().getRequests().getExit())) {
            // exit //TODO
        } else if (msg.equals(commonServiceModule.getConstants().getRequests().getQr())) {
            // qr // TODO
        } else {
            error(request);
        }
    }

    private void error(RocketchatServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getUnknownCommand());
    }

    private void badRequest(RocketchatServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getInvalidRequest());
    }
}
