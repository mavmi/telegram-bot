package mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.WebsocketUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.qr.exception.BadAttemptException;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.qr.exception.ErrorException;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.AbstractWebsocketClientMessageHandler;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.OnResult;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class QrServiceWebsocketMessageHandler extends AbstractWebsocketClientMessageHandler<RocketchatServiceRq> {

    private static final int MAX_ATTEMPTS = 5;

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final PmsUtils pmsUtils;

    private boolean loggedIn = false;
    private int stepNumber = 0;
    private int currentAttempt = 0;

    private UserCaches userCaches;

    private OnResult<RocketchatServiceRq> onSuccess;
    private OnResult<RocketchatServiceRq> onFailure;

    private RocketchatServiceRq request;
    private RocketWebsocketClient websocketClient;
    private CryptoMapper cryptoMapper;
    private TextEncryptor textEncryptor;

    private RocketchatModel model;
    private ConnectRs connectResponse;
    private LoginRs loginResponse;
    private CreateDMRs createDMResponse;
    private SubscribeForMsgUpdatesRs subscribeResponse;

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
        this.cryptoMapper = commonUtils.getCryptoMapper();
        this.textEncryptor = commonUtils.getTextEncryptor();

        runNext(null);
    }

    @Override
    public void runNext(String message) {
        try {
            if (stepNumber == 0) sendConnectRequest();
            else if (stepNumber == 1) handleConnectResponse(message);
            else if (stepNumber == 2) handleLoginResponse(message);
            else if (stepNumber == 3) handleCreateRoomResponse(message);
            else if (stepNumber == 4) handleSubscribeResponse(message);
            else if (stepNumber == 5) handleQrResponse(message);

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
        int msgId = telegramBotUtils.sendText(chatId, e.getMessage());
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
    }

    private void sendConnectRequest() {
        RocketchatModel model = getUserData();
        if (model == null) {
            return;
        } else {
            this.model = model;
        }

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
            sendLoginRequest();
        }
    }

    private void sendLoginRequest() {
        LoginRq loginRequest = commonUtils.getWebsocketClientMapper().generateLoginRequest(model.getRocketchatUsername(), model.getRocketchatPasswordHash());
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
            throw new ErrorException(constants.getPhrases().getCommon().getError() + "\n" + loginResponse.getError().getMessage());
        } else {
            this.loginResponse = loginResponse;
            this.loggedIn = true;
            sendCreateRoomRequest();
        }
    }

    private void sendCreateRoomRequest() {
        CreateDMRq createDmRequest = commonUtils.getWebsocketClientMapper().generateCreateDmRequest(model.getRocketchatUsername());
        websocketClient.sendCreateDmRequest(createDmRequest);
    }

    private void handleCreateRoomResponse(String message) {
        RocketConstants constants = commonUtils.getConstants();
        CreateDMRs createDMResponse = WebsocketUtils.getCreateDmRs(message);

        if (createDMResponse == null || createDMResponse.getResult() == null || createDMResponse.getResult().getRid() == null) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new BadAttemptException();
            } else {
                throw new ErrorException(constants.getPhrases().getCommon().getError());
            }
        } else {
            this.createDMResponse = createDMResponse;
            sendSubscribeRequest();
        }
    }

    private void sendSubscribeRequest() {
        SubscribeForMsgUpdatesRq subscribeRequest = commonUtils.getWebsocketClientMapper().generateSubscribeForMsgUpdatesRequest(loginResponse.getResult().getId());
        websocketClient.sendSubscribeForMessagesUpdatesRequest(subscribeRequest);
    }

    private void handleSubscribeResponse(String message) {
        RocketConstants constants = commonUtils.getConstants();
        SubscribeForMsgUpdatesRs subscribeResponse = WebsocketUtils.getSubscribeForMsgUpdates(message);

        if (subscribeResponse == null) {
            throw new ErrorException(constants.getPhrases().getCommon().getError());
        } else {
            this.subscribeResponse = subscribeResponse;
            sendQrRequest();
        }
    }

    private void sendQrRequest() {
        SendCommandRq sendCommandRequest = commonUtils.getWebsocketClientMapper().generateSendCommandRequest(commonUtils.getQrCommand(), createDMResponse.getResult().getRid());
        websocketClient.sendCommandRequest(sendCommandRequest);
    }

    private void handleQrResponse(String message) {
        long chatId = request.getChatId();
        RocketConstants constants = commonUtils.getConstants();
        MessageChangedNotificationRs messageChangedResponse = WebsocketUtils.getMessageChangedNotification(message);

        if (messageChangedResponse != null && messageChangedResponse.getError() != null) {
            throw new ErrorException(constants.getPhrases().getCommon().getError() + "\n" + messageChangedResponse.getError().getMessage());
        }

        try {
            AtomicReference<String> text = new AtomicReference<>();
            AtomicReference<String> image = new AtomicReference<>();

            messageChangedResponse
                    .getFields()
                    .getArgs()
                    .stream()
                    .flatMap(arg -> arg.getMd().stream())
                    .filter(md -> md.getValue() != null)
                    .flatMap(md -> md.getValue().stream())
                    .filter(value -> value.getValueObj() != null || value.getValueString() != null && !value.getValueString().contains("Generating QR code"))
                    .forEach(value -> {
                        if (value.getType().equals("PLAIN_TEXT")) {
                            text.set(value.getValueString());
                        } else if (value.getType().equals("IMAGE")) {
                            image.set(value.getValueObj().getSrc().getValue());
                        }
                    });

            if (text.get() != null && image.get() != null) {
                closeConnection();
                onSuccess.process(request, createQrFile(image.get()), text.get(), userCaches);
            } else if (text.get() != null && image.get() == null) {
                closeConnection();
                onFailure.process(request, text.get(), userCaches);
            } else {
                if (currentAttempt < MAX_ATTEMPTS) {
                    throw new BadAttemptException();
                } else {
                    throw new ErrorException(constants.getPhrases().getCommon().getError());
                }
            }
        } catch (Exception e) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new BadAttemptException();
            } else {
                log.error(e.getMessage(), e);
                throw new ErrorException(messageChangedResponse.getError().getMessage());
            }
        }
    }

    private void sendLogoutRequest() {
        LogoutRs logoutRequest = commonUtils.getWebsocketClientMapper().generateLogoutRs(null);
        websocketClient.sendLogoutRequest(logoutRequest);
    }

    @Nullable
    private RocketchatModel getUserData() {
        long chatId = request.getChatId();
        Optional<RocketchatModel> modelOptional = commonUtils.getRocketchatRepository().findByTelegramId(chatId);
        RocketConstants constants = commonUtils.getConstants();
        if (modelOptional.isEmpty()) {
            int msgId = telegramBotUtils.sendText(chatId, constants.getPhrases().getAuth().getCredsNotFound());
            telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
            telegramBotUtils.deleteQueuedMessages(chatId, commonUtils.getUserCaches());

            return null;
        }

        return cryptoMapper.decryptRocketchatModel(textEncryptor, modelOptional.get());
    }

    @Nullable
    private File createQrFile(String base64qrCode) {
        String base64file = base64qrCode.split(",")[1];
        byte[] fileBytes = DatatypeConverter.parseBase64Binary(base64file);
        String randomFileName = Utils.generateRandomString() + ".png";
        File file = new File(commonUtils.getOutputDirectoryPath() + "/" + randomFileName);

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(fileBytes);
            return file;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
