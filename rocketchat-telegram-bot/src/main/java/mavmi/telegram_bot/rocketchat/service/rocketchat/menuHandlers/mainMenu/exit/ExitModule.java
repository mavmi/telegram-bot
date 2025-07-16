package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.mainMenu.exit;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.service_api.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExitModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final PmsUtils pmsUtils;

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        init(request);
        deleteIncomingMessage(request);
        exit(request);
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonUtils.getUserCaches().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void exit(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        commonUtils.getDatabaseService().deleteByTelegramId(chatId);
        int msgId = telegramBotUtils.sendText(chatId, commonUtils.getConstants().getPhrases().getCommon().getOk());
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, commonUtils.getUserCaches());
    }
}
