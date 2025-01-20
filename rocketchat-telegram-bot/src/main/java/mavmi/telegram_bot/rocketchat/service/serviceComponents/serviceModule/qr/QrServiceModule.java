package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.qr;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.qr.messageHandler.QrServiceWebsocketMessageHandler;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class QrServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public QrServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.setDefaultServiceMethods(List.of(this::init, this::deleteIncomingMessage, this::inform, this::onDefault));
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getDefaultServiceMethods()) {
            method.process(request);
        }
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void inform(RocketchatServiceRq request) {
        int msgId = commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getQr().getQrIsCreatingResponse());
        commonServiceModule.addMessageToDeleteAfterEnd(msgId);
    }

    private void onDefault(RocketchatServiceRq request) {
        QrServiceWebsocketMessageHandler messageHandler = new QrServiceWebsocketMessageHandler(commonServiceModule);
        RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                request.getChatId(),
                commonServiceModule.getRocketchatUrl(),
                messageHandler,
                commonServiceModule.getConnectionTimeout(),
                commonServiceModule.getAwaitingPeriodMillis(),
                commonServiceModule
        );
        messageHandler.start(
                request,
                websocketClient,
                (req, payload) -> {
                    File qrCodeFile = (File) payload[0];
                    String textMsg = (String) payload[1];

                    long chatId = req.getChatId();
                    File fileToSend = new File(qrCodeFile.getAbsolutePath());
                    commonServiceModule.sendImage(chatId, textMsg, fileToSend);
                    commonServiceModule.deleteQueuedMessages(chatId);
                    fileToSend.delete();
                },
                (req, payload) -> {
                    long chatId = req.getChatId();
                    String textMsg = (String) payload[0];
                    int msgId = commonServiceModule.sendText(chatId, textMsg);
                    commonServiceModule.deleteMessageAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
                    commonServiceModule.deleteQueuedMessages(chatId);
                }
        );
    }
}
