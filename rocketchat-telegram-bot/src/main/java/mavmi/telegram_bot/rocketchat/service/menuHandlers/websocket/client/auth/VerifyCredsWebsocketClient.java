package mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.auth;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;

public class VerifyCredsWebsocketClient extends AbstractAuthWebsocketClient {

    public VerifyCredsWebsocketClient(RocketchatServiceRq request,
                                      UserCaches userCaches,
                                      CommonUtils commonUtils,
                                      TelegramBotUtils telegramBotUtils,
                                      PmsUtils pmsUtils) {
        super(request, userCaches, commonUtils, telegramBotUtils, pmsUtils);
    }

    @Override
    protected void onSuccess(Object... payload) {
        long chatId = request.getChatId();
        int msgId = telegramBotUtils.sendText(chatId, constants.getPhrases().getAuth().getAlreadyLoggedIn());
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
    }

    @Override
    protected void onFailure(Object... payload) {
        long chatId = request.getChatId();
        userCaches.getDataCache(RocketDataCache.class)
                .getMenuHistoryContainer()
                .add(RocketMenu.AUTH_ENTER_LOGIN);
        int msgId = telegramBotUtils.sendText(chatId, constants.getPhrases().getAuth().getEnterLogin());
        commonUtils.addMessageToDeleteAfterEnd(msgId, userCaches);
    }
}
