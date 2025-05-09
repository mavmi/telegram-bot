package mavmi.telegram_bot.rocketchat.service.menuHandlers.mainMenu.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.qr.QrWebsocketClient;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QrModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final PmsUtils pmsUtils;

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        init(request);
        deleteIncomingMessage(request);
        inform(request);
        generateQr(request);
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonUtils.getUserCaches().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void inform(RocketchatServiceRq request) {
        int msgId = telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getQr().getQrIsCreatingResponse());
        commonUtils.addMessageToDeleteAfterEnd(msgId);
    }

    private void generateQr(RocketchatServiceRq request) {
        QrWebsocketClient websocketClient = new QrWebsocketClient(request,
                commonUtils.getUserCaches(),
                commonUtils,
                telegramBotUtils,
                pmsUtils);
        websocketClient.start();
    }
}
