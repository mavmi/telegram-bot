package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.messageHandler.AuthServiceWebsocketMessageHandler;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public AuthServiceModule(CommonServiceModule commonServiceModule) {
        List<ServiceMethod<RocketchatServiceRq>> methodsOnAuth = List.of(this::init, this::onAuth, this::deleteIncomingMessage);

        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getStart(), methodsOnAuth)
                .add(commonServiceModule.getConstants().getRequests().getAuth(), methodsOnAuth);
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getMethods(msg)) {
            method.process(request);
        }
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    public void onAuth(RocketchatServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).getMenuContainer().add(RocketMenu.AUTH_ENTER_LOGIN);
        int msgId = commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getEnterLogin());
        commonServiceModule.addMsgToDeleteAfterEnd(msgId);
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMsgToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    public void doLogin(RocketchatServiceRq request) {
        AuthServiceWebsocketMessageHandler messageHandler = new AuthServiceWebsocketMessageHandler(commonServiceModule);
        RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                commonServiceModule.getRocketchatUrl(),
                messageHandler,
                commonServiceModule.getConnectionTimeout(),
                commonServiceModule.getAwaitingPeriodMillis()
        );
        messageHandler.start(request, websocketClient);
    }
}
