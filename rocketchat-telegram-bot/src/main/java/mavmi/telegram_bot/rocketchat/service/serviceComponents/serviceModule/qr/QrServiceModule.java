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
        commonServiceModule.addMsgToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void inform(RocketchatServiceRq request) {
        int msgId = commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getQrIsCreatingResponse());
        commonServiceModule.addMsgToDeleteAfterEnd(msgId);
    }

    private void onDefault(RocketchatServiceRq request) {
        QrServiceWebsocketMessageHandler messageHandler = new QrServiceWebsocketMessageHandler(commonServiceModule);
        RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                commonServiceModule.getRocketchatUrl(),
                messageHandler,
                commonServiceModule.getConnectionTimeout(),
                commonServiceModule.getAwaitingPeriodMillis()
        );
        messageHandler.start(request, websocketClient);
    }
}
