package mavmi.telegram_bot.rocketchat.service.menuHandlers.mainMenu.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.qr.QrServiceWebsocketMessageHandler;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.stereotype.Component;

import java.io.File;

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
        QrServiceWebsocketMessageHandler messageHandler = new QrServiceWebsocketMessageHandler(commonUtils, telegramBotUtils, pmsUtils);
        RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                request.getChatId(),
                commonUtils.getRocketchatUrl(),
                messageHandler,
                pmsUtils.getConnectionTimeout(),
                pmsUtils.getAwaitingPeriodMillis(),
                commonUtils
        );
        messageHandler.start(
                commonUtils.getUserCaches(),
                request,
                websocketClient,
                (req, payload) -> {
                    File qrCodeFile = (File) payload[0];
                    String textMsg = (String) payload[1];
                    UserCaches userCaches = (UserCaches) payload[2];

                    long chatId = req.getChatId();
                    File fileToSend = new File(qrCodeFile.getAbsolutePath());
                    telegramBotUtils.sendImage(chatId, textMsg, fileToSend);
                    telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
                    fileToSend.delete();
                },
                (req, payload) -> {
                    long chatId = req.getChatId();
                    String textMsg = (String) payload[0];
                    UserCaches userCaches = (UserCaches) payload[2];

                    int msgId = telegramBotUtils.sendText(chatId, textMsg);
                    telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
                    telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
                }
        );
    }
}
