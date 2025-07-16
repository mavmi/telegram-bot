package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.ConnectRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.ConnectRs;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.LoginRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.WebsocketUtils;
import mavmi.telegram_bot.rocketchat.webscoket.api.exception.WebsocketBadAttemptException;
import mavmi.telegram_bot.rocketchat.webscoket.api.exception.WebsocketErrorException;
import mavmi.telegram_bot.rocketchat.webscoket.impl.AbstractWebsocketClient;

@Slf4j
public abstract class AbstractAuthWebsocketClient extends AbstractWebsocketClient {

    protected final RocketConstants constants;

    protected ConnectRs connectResponse;
    protected LoginRs loginResponse;

    protected int stepNumber = 0;
    protected int currentAttempt = 0;

    public AbstractAuthWebsocketClient(RocketchatServiceRq request,
                                       UserCaches userCaches,
                                       CommonUtils commonUtils,
                                       TelegramBotUtils telegramBotUtils,
                                       PmsUtils pmsUtils) {
        super(request,
                userCaches,
                commonUtils,
                telegramBotUtils,
                pmsUtils);
        this.constants = commonUtils.getConstants();
    }

    @Override
    public void start() {
        this.onMessage("");
    }

    @Override
    public void onMessage(String message) {
        try {
            if (stepNumber == 0) sendConnectRequest();
            else if (stepNumber == 1) handleConnectResponse(message);
            else if (stepNumber == 2) handleLoginResponse(message);

            currentAttempt = 0;
            stepNumber++;
        } catch (WebsocketBadAttemptException e) {
            onBadAttempt();
        } catch (WebsocketErrorException e) {
            onError(e);
        }
    }

    @SneakyThrows
    private void sendConnectRequest() {
        this.connect();

        long awaitingMillis = 0;
        long connectionTimeout = pmsUtils.getConnectionTimeout();
        long awaitingPeriodMillis = pmsUtils.getAwaitingPeriodMillis();
        while (!this.isOpen() && awaitingMillis < connectionTimeout * 1000) {
            try {
                Thread.sleep(awaitingPeriodMillis);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            awaitingMillis += awaitingPeriodMillis;
        }

        if (this.isOpen()) {
            ConnectRq connectRequest = websocketClientMapper.generateConnectRequest("null");
            this.send(OBJECT_MAPPER.writeValueAsString(connectRequest));
        }
    }

    private void handleConnectResponse(String message) {
        ConnectRs connectResponse = WebsocketUtils.getConnectRs(message);

        if (connectResponse == null) {
            throw new WebsocketErrorException(constants.getPhrases().getCommon().getError());
        } else {
            this.connectResponse = connectResponse;
            this.sendLoginRequest();
        }
    }

    @SneakyThrows
    private void sendLoginRequest() {
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);

        LoginRq loginRequest = websocketClientMapper.generateLoginRequest(dataCache.getRocketchatUsername(), dataCache.getRocketchatPasswordHash());
        this.send(OBJECT_MAPPER.writeValueAsString(loginRequest));
    }

    private void handleLoginResponse(String message) {
        LoginRs loginResponse = WebsocketUtils.getLoginRs(message);

        if (loginResponse == null || (loginResponse.getResult() == null && loginResponse.getError() == null)) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new WebsocketBadAttemptException();
            } else {
                throw new WebsocketErrorException(constants.getPhrases().getCommon().getError());
            }
        } else if (loginResponse.getError() != null) {
            this.loginResponse = loginResponse;
            this.loggedIn = false;
            this.closeConnection();
            onFailure();
        } else {
            this.loginResponse = loginResponse;
            this.loggedIn = true;
            this.closeConnection();
            onSuccess();
        }
    }

    private void onBadAttempt() {
        currentAttempt++;
    }
}
