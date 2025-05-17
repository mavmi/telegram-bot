package mavmi.telegram_bot.rocketchat.webscoket.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.mapper.WebsocketClientMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LogoutRs;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public abstract class AbstractWebsocketClient extends WebSocketClient {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final int MAX_ATTEMPTS = 5;

    protected final RocketchatServiceRq request;
    protected final UserCaches userCaches;
    protected final CommonUtils commonUtils;
    protected final TelegramBotUtils telegramBotUtils;
    protected final PmsUtils pmsUtils;
    protected final WebsocketClientMapper websocketClientMapper;
    protected final String url;

    protected boolean loggedIn;

    public AbstractWebsocketClient(RocketchatServiceRq request,
                                   UserCaches userCaches,
                                   CommonUtils commonUtils,
                                   TelegramBotUtils telegramBotUtils,
                                   PmsUtils pmsUtils) {
        super(URI.create(commonUtils.getRocketchatUrl()));
        this.request = request;
        this.userCaches = userCaches;
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
        this.pmsUtils = pmsUtils;
        this.websocketClientMapper = commonUtils.getWebsocketClientMapper();
        this.url = commonUtils.getRocketchatUrl();
    }

    public abstract void start();

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Connection opened with server {}", url);
    }

    @Override
    public abstract void onMessage(String message);

    @Override
    public void onError(Exception e) {
        closeConnection();

        long chatId = request.getChatId();
        int msgId = telegramBotUtils.sendText(chatId, e.getMessage());
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Connection closed with status code {}", code);
    }

    protected void closeConnection() {
        if (loggedIn) {
            sendLogoutRequest();
        }

        this.close();
    }

    @SneakyThrows
    protected void sendLogoutRequest() {
        LogoutRs logoutRequest = websocketClientMapper.generateLogoutRs(null);
        this.send(OBJECT_MAPPER.writeValueAsString(logoutRequest));
    }

    protected abstract void onSuccess(Object... payload);

    protected abstract void onFailure(Object... payload);
}
