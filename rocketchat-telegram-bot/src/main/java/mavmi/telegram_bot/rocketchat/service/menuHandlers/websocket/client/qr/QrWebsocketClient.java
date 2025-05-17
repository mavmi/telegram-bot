package mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.qr;

import lombok.SneakyThrows;
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
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.webscoket.api.exception.WebsocketBadAttemptException;
import mavmi.telegram_bot.rocketchat.webscoket.api.exception.WebsocketErrorException;
import mavmi.telegram_bot.rocketchat.webscoket.impl.AbstractWebsocketClient;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class QrWebsocketClient extends AbstractWebsocketClient {

    private final CryptoMapper cryptoMapper;
    private final TextEncryptor textEncryptor;

    private RocketchatModel userDbModel;
    private ConnectRs connectResponse;
    private LoginRs loginResponse;
    private CreateDMRs createDMResponse;
    private SubscribeForMsgUpdatesRs subscribeResponse;

    private int stepNumber = 0;
    private int currentAttempt = 0;

    public QrWebsocketClient(RocketchatServiceRq request,
                             UserCaches userCaches,
                             CommonUtils commonUtils,
                             TelegramBotUtils telegramBotUtils,
                             PmsUtils pmsUtils) {
        super(request,
                userCaches,
                commonUtils,
                telegramBotUtils,
                pmsUtils);
        this.cryptoMapper = commonUtils.getCryptoMapper();
        this.textEncryptor = commonUtils.getTextEncryptor();
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
            else if (stepNumber == 3) handleCreateRoomResponse(message);
            else if (stepNumber == 4) handleSubscribeResponse(message);
            else if (stepNumber == 5) handleQrResponse(message);

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
        RocketchatModel userDbModel = getUserData();
        if (userDbModel == null) {
            return;
        } else {
            this.userDbModel = userDbModel;
        }

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
            ConnectRq connectRequest = commonUtils.getWebsocketClientMapper().generateConnectRequest("null");
            send(OBJECT_MAPPER.writeValueAsString(connectRequest));
        }
    }

    private void handleConnectResponse(String message) {
        ConnectRs connectResponse = WebsocketUtils.getConnectRs(message);

        if (connectResponse == null) {
            throw new WebsocketErrorException(commonUtils.getConstants().getPhrases().getCommon().getError());
        } else {
            this.connectResponse = connectResponse;
            sendLoginRequest();
        }
    }

    @SneakyThrows
    private void sendLoginRequest() {
        LoginRq loginRequest = commonUtils.getWebsocketClientMapper().generateLoginRequest(userDbModel.getRocketchatUsername(),
                userDbModel.getRocketchatPasswordHash());
        send(OBJECT_MAPPER.writeValueAsString(loginRequest));
    }

    private void handleLoginResponse(String message) {
        RocketConstants constants = commonUtils.getConstants();
        LoginRs loginResponse = WebsocketUtils.getLoginRs(message);

        if (loginResponse == null || (loginResponse.getResult() == null && loginResponse.getError() == null)) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new WebsocketBadAttemptException();
            } else {
                throw new WebsocketErrorException(constants.getPhrases().getCommon().getError());
            }
        } else if (loginResponse.getError() != null) {
            throw new WebsocketErrorException(constants.getPhrases().getCommon().getError() + "\n" + loginResponse.getError().getMessage());
        } else {
            this.loginResponse = loginResponse;
            this.loggedIn = true;
            sendCreateRoomRequest();
        }
    }

    @SneakyThrows
    private void sendCreateRoomRequest() {
        CreateDMRq createDmRequest = commonUtils.getWebsocketClientMapper().generateCreateDmRequest(userDbModel.getRocketchatUsername());
        send(OBJECT_MAPPER.writeValueAsString(createDmRequest));
    }

    private void handleCreateRoomResponse(String message) {
        RocketConstants constants = commonUtils.getConstants();
        CreateDMRs createDMResponse = WebsocketUtils.getCreateDmRs(message);

        if (createDMResponse == null || createDMResponse.getResult() == null || createDMResponse.getResult().getRid() == null) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new WebsocketBadAttemptException();
            } else {
                throw new WebsocketErrorException(constants.getPhrases().getCommon().getError());
            }
        } else {
            this.createDMResponse = createDMResponse;
            sendSubscribeRequest();
        }
    }

    @SneakyThrows
    private void sendSubscribeRequest() {
        SubscribeForMsgUpdatesRq subscribeRequest = commonUtils.getWebsocketClientMapper().generateSubscribeForMsgUpdatesRequest(loginResponse.getResult().getId());
        send(OBJECT_MAPPER.writeValueAsString(subscribeRequest));
    }

    private void handleSubscribeResponse(String message) {
        RocketConstants constants = commonUtils.getConstants();
        SubscribeForMsgUpdatesRs subscribeResponse = WebsocketUtils.getSubscribeForMsgUpdates(message);

        if (subscribeResponse == null) {
            throw new WebsocketErrorException(constants.getPhrases().getCommon().getError());
        } else {
            this.subscribeResponse = subscribeResponse;
            sendQrRequest();
        }
    }

    @SneakyThrows
    private void sendQrRequest() {
        SendCommandRq sendCommandRequest = commonUtils.getWebsocketClientMapper().generateSendCommandRequest(commonUtils.getQrCommand(), createDMResponse.getResult().getRid());
        send(OBJECT_MAPPER.writeValueAsString(sendCommandRequest));
    }

    private void handleQrResponse(String message) {
        long chatId = request.getChatId();
        RocketConstants constants = commonUtils.getConstants();
        MessageChangedNotificationRs messageChangedResponse = WebsocketUtils.getMessageChangedNotification(message);

        if (messageChangedResponse != null && messageChangedResponse.getError() != null) {
            throw new WebsocketErrorException(constants.getPhrases().getCommon().getError() + "\n" + messageChangedResponse.getError().getMessage());
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
                onSuccess(createQrFile(image.get()), text.get());
            } else if (text.get() != null && image.get() == null) {
                closeConnection();
                onFailure(text.get());
            } else {
                if (currentAttempt < MAX_ATTEMPTS) {
                    throw new WebsocketBadAttemptException();
                } else {
                    throw new WebsocketErrorException(constants.getPhrases().getCommon().getError());
                }
            }
        } catch (Exception e) {
            if (currentAttempt < MAX_ATTEMPTS) {
                throw new WebsocketBadAttemptException();
            } else {
                log.error(e.getMessage(), e);
                throw new WebsocketErrorException(messageChangedResponse.getError().getMessage());
            }
        }
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

    private void onBadAttempt() {
        currentAttempt++;
    }

    @Override
    protected void onSuccess(Object... payload) {
        File qrCodeFile = (File) payload[0];
        String textMsg = (String) payload[1];

        long chatId = request.getChatId();
        File fileToSend = new File(qrCodeFile.getAbsolutePath());
        telegramBotUtils.sendImage(chatId, textMsg, fileToSend);
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
        fileToSend.delete();
    }

    @Override
    protected void onFailure(Object... payload) {
        long chatId = request.getChatId();
        String textMsg = (String) payload[0];

        int msgId = telegramBotUtils.sendText(chatId, textMsg);
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
    }
}
