package mavmi.telegram_bot.rocketchat.websocket.api.messageHandler;

import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;

public abstract class AbstractWebsocketClientMessageHandler<RequestType extends ServiceRequest> {

    public abstract void start(RequestType request, RocketWebsocketClient websocketClient, OnResult<RequestType> onSuccess, OnResult<RequestType> onFailure);

    public abstract void runNext(String message);
}
