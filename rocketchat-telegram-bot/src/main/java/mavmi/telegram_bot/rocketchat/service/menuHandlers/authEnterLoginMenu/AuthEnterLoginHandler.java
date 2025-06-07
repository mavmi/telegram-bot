package mavmi.telegram_bot.rocketchat.service.menuHandlers.authEnterLoginMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthEnterLoginHandler extends MenuRequestHandler<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;


    public AuthEnterLoginHandler(MenuEngine menuEngine,
                                 CommonUtils commonUtils,
                                 TelegramBotUtils telegramBotUtils) {
        super(menuEngine, RocketMenu.AUTH_ENTER_LOGIN);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        getLogin(request);
        deleteIncomingMessage(request);
    }

    private void getLogin(RocketchatServiceRq request) {
        RocketDataCache dataCache = commonUtils.getUserCaches().getDataCache(RocketDataCache.class);
        dataCache.setRocketchatUsername(request.getMessageJson().getTextMessage());
        dataCache.getMenuHistoryContainer().add(RocketMenu.AUTH_ENTER_PASSWORD);

        int msgId = telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getAuth().getEnterPassword());
        commonUtils.addMessageToDeleteAfterEnd(msgId);
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }
}
