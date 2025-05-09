package mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.auth;

import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.WebsocketUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.auth.exception.ErrorException;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.qr.exception.BadAttemptException;
import mavmi.telegram_bot.rocketchat.webscoket.api.MessageHandler;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component("authMessageHandler")
public class AuthMessageHandler implements MessageHandler {

    // TODO delete
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
            ConnectRq connectRequest = commonUtils.getWebsocketClientMapper().generateConnectRequest("null");
            websocketClient.sendConnectRequest(connectRequest);
        }
    }

    @Nullable
    private void handleConnectResponse(String message) {
        ConnectRs connectResponse = WebsocketUtils.getConnectRs(message);

        if (connectResponse == null) {
            throw new ErrorException(commonUtils.getConstants().getPhrases().getCommon().getError());
        } else {
            this.connectResponse = connectResponse;
        }
    }

    // TODO delete
    private void sendLoginRequest() {
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);
        Creds creds = dataCache.getCreds();

        LoginRq loginRequest = commonUtils.getWebsocketClientMapper().generateLoginRequest(creds.getRocketchatUsername(), creds.getRocketchatPasswordHash());
        websocketClient.sendLoginRequest(loginRequest);
    }

    private void handleLoginResponse(String message) {
        RocketConstants constants = commonUtils.getConstants();
        LoginRs loginResponse = WebsocketUtils.getLoginRs(message);

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

    // TODO delete
    private void sendLogoutRequest() {
        LogoutRs logoutRequest = commonUtils.getWebsocketClientMapper().generateLogoutRs(null);
        websocketClient.sendLogoutRequest(logoutRequest);
    }
}
