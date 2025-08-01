package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;

public class VerifyCredsWebsocketClient extends AbstractAuthWebsocketClient {

    public VerifyCredsWebsocketClient(RocketchatServiceRq request,
                                      UserCaches userCaches,
                                      CommonUtils commonUtils,
                                      TelegramBotUtils telegramBotUtils,
                                      PmsUtils pmsUtils,
                                      AUTH_MODE authMode) {
        super(request, userCaches, commonUtils, telegramBotUtils, pmsUtils, authMode);
    }

    @Override
    protected void onSuccess(Object... payload) {
        long chatId = request.getChatId();
        int msgId = telegramBotUtils.sendTextDeleteKeyboard(chatId, constants.getPhrases().getAuth().getAlreadyLoggedIn());
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
        userCaches.getDataCache(RocketDataCache.class).resetCreds();
        commonUtils.dropUserMenu(userCaches);
    }

    @Override
    protected void onFailure(Object... payload) {
        long chatId = request.getChatId();
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);
        Menu nextMenu = (authMode == AUTH_MODE.TOKEN) ? RocketMenu.AUTH_ENTER_TOKEN : RocketMenu.AUTH_ENTER_LOGIN;
        String msgToSend = (authMode == AUTH_MODE.TOKEN) ? commonUtils.getConstants().getPhrases().getAuth().getEnterToken() :
                commonUtils.getConstants().getPhrases().getAuth().getEnterLogin();

        dataCache.getMenuHistoryContainer().add(nextMenu);
        int msgId = telegramBotUtils.sendTextDeleteKeyboard(chatId, msgToSend);
        commonUtils.addMessageToDeleteAfterEnd(msgId, userCaches);
        userCaches.getDataCache(RocketDataCache.class).resetCreds();
    }
}
