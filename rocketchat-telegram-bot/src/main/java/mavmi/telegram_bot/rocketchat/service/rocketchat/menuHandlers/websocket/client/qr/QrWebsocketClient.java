package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.qr;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.database.dto.RocketchatDto;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.messageChangedNotification.MessageChangedNotificationArg;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.WebsocketUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth.AUTH_MODE;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.webscoket.api.exception.WebsocketBadAttemptException;
import mavmi.telegram_bot.rocketchat.webscoket.api.exception.WebsocketErrorException;
import mavmi.telegram_bot.rocketchat.webscoket.impl.AbstractWebsocketClient;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class QrWebsocketClient extends AbstractWebsocketClient {

    private final CryptoMapper cryptoMapper;
    private final TextEncryptor textEncryptor;

    private RocketchatDto userDto;
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
        log.info("QR message received: {}", message);

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
        RocketchatDto dto = getUserData();
        if (dto == null) {
            return;
        } else {
            this.userDto = dto;
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
        AUTH_MODE authMode = (userDto.getRocketchatToken() != null) ? AUTH_MODE.TOKEN : AUTH_MODE.PASSWORD;

        LoginRq loginRequest = (authMode == AUTH_MODE.PASSWORD) ?
                websocketClientMapper.generateLoginRequest(userDto.getRocketchatUsername(), userDto.getRocketchatPasswordHash()) :
                websocketClientMapper.generateLoginRequest(userDto.getRocketchatToken());

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
        CreateDMRq createDmRequest = commonUtils.getWebsocketClientMapper().generateCreateDmRequest("qr-code-generator.bot");
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
        SubscribeForMsgUpdatesRq subscribeRequest = commonUtils.getWebsocketClientMapper().generateSubscribeForMsgUpdatesRequest(createDMResponse.getResult().getRid());
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
            MessageChangedNotificationArg arg = messageChangedResponse
                    .getFields()
                    .getArgs()
                    .get(0);

            if (arg.getFile() != null) {
                onSuccess(createQrFile1(arg.getFile().getId()), null);
                stepNumber--;
            } else if (arg.getMsg() != null) {
                telegramBotUtils.sendText(chatId, arg.getMsg());
                if (!arg.getMsg().contains("The QR code will expire on")) {
                    stepNumber--;
                } else {
                    closeConnection();
                }
            }  else {
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
    private RocketchatDto getUserData() {
        long chatId = request.getChatId();
        RocketchatDto dto = commonUtils.getDatabaseService().findByTelegramId(chatId);
        RocketConstants constants = commonUtils.getConstants();
        if (dto == null) {
            int msgId = telegramBotUtils.sendText(chatId, constants.getPhrases().getAuth().getCredsNotFound());
            telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
            telegramBotUtils.deleteQueuedMessages(chatId, commonUtils.getUserCaches());

            return null;
        }

        return cryptoMapper.decryptRocketchatDto(textEncryptor, dto);
    }

    private File createQrFile1(String imageUrl) {
        String randomFileName = Utils.generateRandomString() + ".png";
        File file = new File(commonUtils.getOutputDirectoryPath() + "/" + randomFileName);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rocketchat-student.21-school.ru/file-upload/" + imageUrl + "/qr-code.png"))
                .header("X-User-Id", loginResponse.getResult().getId())
                .header("X-Auth-Token", loginResponse.getResult().getToken())
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            try (InputStream inputStream = response.body()) {
                Files.copy(
                        inputStream,
                        Path.of(file.getAbsolutePath()),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new WebsocketBadAttemptException();
        }

        return file;
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

        int newQrMsgId = 0;
        if (textMsg != null) {
            newQrMsgId = telegramBotUtils.sendImage(chatId, textMsg, fileToSend);
        } else {
            newQrMsgId = telegramBotUtils.sendImage(chatId, " ", fileToSend);
        }
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);

        Integer lastQrMsgId = userDto.getLastQrMsgId();
        if (lastQrMsgId != null) {
            telegramBotUtils.deleteMessage(chatId, userDto.getLastQrMsgId());
        }

        if (textMsg != null) {
            commonUtils.getDatabaseService().updateLastQrMsgId(chatId, newQrMsgId);
        }

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
