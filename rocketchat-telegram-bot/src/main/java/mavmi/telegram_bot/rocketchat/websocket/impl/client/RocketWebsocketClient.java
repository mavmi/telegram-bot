package mavmi.telegram_bot.rocketchat.websocket.impl.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.LogsWebsocketModel;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.AbstractWebsocketClientMessageHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Queue;

@Getter
@Setter
@Slf4j
public class RocketWebsocketClient extends WebSocketClient {

    private final String url;
    private final long chatId;
    private final long connectionTimeout;
    private final long awaitingPeriodMillis;
    private final Queue<String> messagesQueue;
    private final CommonServiceModule commonServiceModule;
    private final AbstractWebsocketClientMessageHandler<?> messageHandler;

    public static RocketWebsocketClient build(
            long chatId,
            String url,
            AbstractWebsocketClientMessageHandler<?> messageHandler,
            long connectionTimeout,
            long awaitingPeriodMillis,
            CommonServiceModule commonServiceModule
    ) {
        return new RocketWebsocketClient(chatId, messageHandler, url, connectionTimeout, awaitingPeriodMillis, commonServiceModule);
    }

    RocketWebsocketClient(
            long chatId,
            AbstractWebsocketClientMessageHandler<?> messageHandler,
            String url,
            long connectionTimeout,
            long awaitingPeriodMillis,
            CommonServiceModule commonServiceModule
    ) {
        super(URI.create(url));
        this.url = url;
        this.chatId = chatId;
        this.messageHandler = messageHandler;
        this.connectionTimeout = connectionTimeout;
        this.awaitingPeriodMillis = awaitingPeriodMillis;
        this.commonServiceModule = commonServiceModule;
        this.messagesQueue = new ArrayDeque<>();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connection opened with {}", url);
    }

    @Override
    public void onMessage(String s) {
        LogsWebsocketModel model = LogsWebsocketModel.builder()
                        .chatid(chatId)
                        .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                        .message(s)
                        .build();
        commonServiceModule.getLogsWebsocketRepository().save(commonServiceModule.getCryptoMapper().encryptLogsWebsocketModel(commonServiceModule.getTextEncryptor(), model));

        messageHandler.runNext(s);
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

    @SneakyThrows
    public void sendLogoutRequest(LogoutRs request) {
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
