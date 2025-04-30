package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.messageHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.messageHandler.exception.ErrorException;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.qr.messageHandler.exception.BadAttemptException;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.AbstractWebsocketClientMessageHandler;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.OnResult;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.lang.Nullable;

@Slf4j
@RequiredArgsConstructor
public class AuthServiceWebsocketMessageHandler extends AbstractWebsocketClientMessageHandler<RocketchatServiceRq> {

    private static final int MAX_ATTEMPTS = 5;

    private final CommonServiceModule commonServiceModule;

    private UserCaches userCaches;

    private OnResult<RocketchatServiceRq> onSuccess;
    private OnResult<RocketchatServiceRq> onFailure;

    private RocketchatServiceRq request;
    private RocketWebsocketClient websocketClient;

    private boolean loggedIn = false;
    private int stepNumber = 0;
    private int currentAttempt = 0;

    private ConnectRs connectResponse;
    private LoginRs loginResponse;

    @Override
    public void start(UserCaches userCaches,
                      RocketchatServiceRq request,
                      RocketWebsocketClient websocketClient,
                      OnResult<RocketchatServiceRq> onSuccess,
                      OnResult<RocketchatServiceRq> onFailure) {
        this.userCaches = userCaches;
        this.request = request;
        this.websocketClient = websocketClient;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;

        runNext(null);
    }

    @Override
    public void runNext(String message) {
        try {
            if (stepNumber == 0) sendConnectRequest();
            else if (stepNumber == 1) handleConnectResponse(message);
            else if (stepNumber == 2) handleLoginResponse(message);

            currentAttempt = 0;
            stepNumber++;
        } catch (BadAttemptException e) {
            onBadAttempt();
        } catch (ErrorException e) {
            onError(e);
        }
    }

    @Override
    public void closeConnection() {
        if (loggedIn) {
            sendLogoutRequest();
        }
        websocketClient.close();
    }

    private void onBadAttempt() {
        currentAttempt++;
    }

    private void onError(ErrorException e) {
        closeConnection();

        long chatId = request.getChatId();
        int msgId = commonServiceModule.sendText(chatId, e.getMessage());
        commonServiceModule.deleteMessageAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
        commonServiceModule.deleteQueuedMessages(chatId);
    }

    private void sendConnectRequest() {
        websocketClient.connect();

        long awaitingMillis = 0;
        long connectionTimeout = websocketClient.getConnectionTimeout();
        long awaitingPeriodMillis = websocketClient.getAwaitingPeriodMillis();
        while (!websocketClient.isOpen() && awaitingMillis < connectionTimeout * 1000) {
            try {
                Thread.sleep(awaitingPeriodMillis);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            awaitingMillis += awaitingPeriodMillis;
        }

        if (websocketClient.isOpen()) {
            ConnectRq connectRequest = commonServiceModule.getWebsocketClientMapper().generateConnectRequest("null");
            websocketClient.sendConnectRequest(connectRequest);
        }
    }

    @Nullable
    private void handleConnectResponse(String message) {
        ConnectRs connectResponse = commonServiceModule.getConnectRs(message);

        if (connectResponse == null) {
            throw new ErrorException(commonServiceModule.getConstants().getPhrases().getCommon().getError());
        } else {
            this.connectResponse = connectResponse;
            sendLoginRequest();
        }
    }

    private void sendLoginRequest() {
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);
        Creds creds = dataCache.getCreds();

        LoginRq loginRequest = commonServiceModule.getWebsocketClientMapper().generateLoginRequest(creds.getRocketchatUsername(), creds.getRocketchatPasswordHash());
        websocketClient.sendLoginRequest(loginRequest);
    }

    private void handleLoginResponse(String message) {
        RocketConstants constants = commonServiceModule.getConstants();
        LoginRs loginResponse = commonServiceModule.getLoginRs(message);

        if (loginResponse == null || (loginResponse.getResult() == null && loginResponse.getError() == null)) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new BadAttemptException();
            } else {
                throw new ErrorException(constants.getPhrases().getCommon().getError());
            }
        } else if (loginResponse.getError() != null) {
            closeConnection();
            onFailure.process(request, constants.getPhrases().getCommon().getError() + "\n" + loginResponse.getError().getMessage(), userCaches);
        } else {
            this.loginResponse = loginResponse;
            this.loggedIn = true;
            closeConnection();
            onSuccess.process(request, loginResponse, userCaches);
        }
    }

    private void sendLogoutRequest() {
        LogoutRs logoutRequest = commonServiceModule.getWebsocketClientMapper().generateLogoutRs(null);
        websocketClient.sendLogoutRequest(logoutRequest);
    }
}
