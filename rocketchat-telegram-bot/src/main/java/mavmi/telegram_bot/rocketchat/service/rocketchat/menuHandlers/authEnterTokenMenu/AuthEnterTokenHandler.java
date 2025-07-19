package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.authEnterTokenMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth.AUTH_MODE;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth.AuthWebsocketClient;
import org.springframework.stereotype.Component;

@Component
public class AuthEnterTokenHandler extends MenuRequestHandler<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final PmsUtils pmsUtils;

    public AuthEnterTokenHandler(MenuEngine menuEngine,
                                 CommonUtils commonUtils,
                                 TelegramBotUtils telegramBotUtils,
                                 PmsUtils pmsUtils) {
        super(menuEngine, RocketMenu.AUTH_ENTER_TOKEN);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
        this.pmsUtils = pmsUtils;
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        getToken(request);
        deleteIncomingMessage(request);
        doLogin(request);
    }

    private void getToken(RocketchatServiceRq request) {
        String token = request.getMessageJson().getTextMessage();
        RocketDataCache dataCache = commonUtils.getUserCaches().getDataCache(RocketDataCache.class);
        dataCache.setRocketchatToken(token);
        doLogin(request);
        commonUtils.dropUserMenu();
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
        telegramBotUtils.deleteQueuedMessages(request.getChatId(), commonUtils.getUserCaches());
    }

    private void doLogin(RocketchatServiceRq request) {
        AuthWebsocketClient websocketClient = new AuthWebsocketClient(request,
                commonUtils.getUserCaches(),
                commonUtils,
                telegramBotUtils,
                pmsUtils,
                AUTH_MODE.TOKEN);
        websocketClient.start();
    }
}
