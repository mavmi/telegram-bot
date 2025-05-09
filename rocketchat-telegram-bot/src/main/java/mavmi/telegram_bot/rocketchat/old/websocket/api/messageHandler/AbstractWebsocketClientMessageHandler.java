package mavmi.telegram_bot.rocketchat.old.websocket.api.messageHandler;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.old.websocket.impl.client.RocketWebsocketClient;

public abstract class AbstractWebsocketClientMessageHandler<RequestType extends ServiceRequest> {

    public abstract void start(UserCaches userCaches, RequestType request, RocketWebsocketClient websocketClient, OnResult<RequestType> onSuccess, OnResult<RequestType> onFailure);

    public abstract void runNext(String message);

    public abstract void closeConnection();
}
