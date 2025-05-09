package mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.auth;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.rocketchat.webscoket.api.MessageHandler;
import mavmi.telegram_bot.rocketchat.webscoket.impl.AbstractWebsocketClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthWebsocketClient extends AbstractWebsocketClient {

    private final MessageHandler messageHandler;

    public AuthWebsocketClient(@Qualifier("authMessageHandler") MessageHandler messageHandler,
                               @Value("websocket.client.url") String url) {
        super(url);
        this.messageHandler = messageHandler;
    }

    @Override
    public void run(ServiceRequest request) {

    }

    @Override
    public void onMessage(String message) {

    }
}
