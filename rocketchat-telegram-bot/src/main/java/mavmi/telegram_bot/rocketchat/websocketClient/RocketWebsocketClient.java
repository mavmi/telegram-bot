package mavmi.telegram_bot.rocketchat.websocketClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
public class RocketWebsocketClient extends WebSocketClient {

    private final long connectionTimeout;
    private final long awaitingPeriodMillis;
    private final String url;
    private final Queue<String> messagesQueue;

    RocketWebsocketClient(
            String url,
            long connectionTimeout,
            long awaitingPeriodMillis
    ) {
        super(URI.create(url));
        this.url = url;
        this.connectionTimeout = connectionTimeout;
        this.awaitingPeriodMillis = awaitingPeriodMillis;
        this.messagesQueue = new ArrayDeque<>();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connection opened with {}", url);
    }

    @Override
    public void onMessage(String s) {
        log.info("New message received");
        messagesQueue.add(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("Connection closed. URL: {}; status code: {}, info: {}", url, i, s);
    }

    @Override
    public void onError(Exception e) {
        log.error(e.getMessage(), e);
    }

    @Nullable
    public String waitForMessage() {
        long awaitingMillis = 0;

        while (awaitingMillis < connectionTimeout * 1000) {
            if (!messagesQueue.isEmpty()) {
                return messagesQueue.remove();
            }

            try {
                Thread.sleep(awaitingPeriodMillis);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            awaitingMillis += awaitingPeriodMillis;
        }

        return null;
    }

    public void sendConnectRequest(ConnectRq request) {
        this.send(convertDtoToStringMessage(request));
    }

    public void sendLoginRequest(LoginRq request) {
        this.send(convertDtoToStringMessage(request));
    }

    public void sendCreateDmRequest(CreateDMRq request) {
        this.send(convertDtoToStringMessage(request));
    }

    public void sendSubscribeForMessagesUpdatesRequest(SubscribeForMsgUpdatesRq request) {
        this.send(convertDtoToStringMessage(request));
    }

    public void sendCommandRequest(SendCommandRq request) {
        this.send(convertDtoToStringMessage(request));
    }

    @Nullable
    private String convertDtoToStringMessage(Object dto) {
        try {
            return new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
