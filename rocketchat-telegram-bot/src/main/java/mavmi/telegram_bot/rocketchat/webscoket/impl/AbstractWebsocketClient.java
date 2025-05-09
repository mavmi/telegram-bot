package mavmi.telegram_bot.rocketchat.webscoket.impl;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public abstract class AbstractWebsocketClient extends WebSocketClient {

    protected final String url;

    public AbstractWebsocketClient(String url) {
        super(URI.create(url));
        this.url = url;
    }

    public abstract void run(ServiceRequest request);

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Connection opened with server {}", url);
    }

    @Override
    public abstract void onMessage(String message);

    @Override
    public void onError(Exception ex) {
        log.error(ex.getMessage(), ex);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Connection closed with status code {}", code);
    }
}
