package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketWebsocketClient;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketWebsocketClientBuilder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketCommunicationServiceModule {

    private static final int MAX_ATTEMPTS = 5;

    private final CommonServiceModule commonServiceModule;

    @Nullable
    public ConnectRs connect(RocketWebsocketClient websocketClient) {
        RocketWebsocketClientBuilder websocketClientBuilder = commonServiceModule.getWebsocketClientBuilder();
        ConnectRq connectRequest = commonServiceModule.getWebsocketClientMapper().generateConnectRequest("null");

        websocketClient.connect();
        long awaitingMillis = 0;
        long connectionTimeout = websocketClientBuilder.getConnectionTimeout();
        long awaitingPeriodMillis = websocketClientBuilder.getAwaitingPeriodMillis();
        while (!websocketClient.isOpen() && awaitingMillis < connectionTimeout * 1000) {
            try {
                Thread.sleep(awaitingPeriodMillis);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            awaitingMillis += awaitingPeriodMillis;
        }
        if (!websocketClient.isOpen()) {
            return null;
        }

        websocketClient.sendConnectRequest(connectRequest);
        String response = websocketClient.waitForMessage();

        return commonServiceModule.getConnectRs(response);
    }

    @Nullable
    public LoginRs login(RocketWebsocketClient websocketClient, String rocketchatUsername, String rocketchatPasswordHash) {
        LoginRq loginRequest = commonServiceModule.getWebsocketClientMapper().generateLoginRequest(rocketchatUsername, rocketchatPasswordHash);
        websocketClient.sendLoginRequest(loginRequest);

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String response = websocketClient.waitForMessage();
            LoginRs loginResponse = commonServiceModule.getLoginRs(response);
            if (loginResponse.getResult() != null || loginResponse.getError() != null) {
                return loginResponse;
            }
        }

        return null;
    }

    @Nullable
    public CreateDMRs createRoom(RocketWebsocketClient websocketClient, String dmUsername) {
        CreateDMRq createDmRequest = commonServiceModule.getWebsocketClientMapper().generateCreateDmRequest(dmUsername);
        websocketClient.sendCreateDmRequest(createDmRequest);

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String response = websocketClient.waitForMessage();
            CreateDMRs createDMResponse = commonServiceModule.getCreateDmRs(response);
            if (createDMResponse != null && createDMResponse.getResult() != null && createDMResponse.getResult().getRid() != null) {
                return createDMResponse;
            }
        }

        return null;
    }

    @Nullable
    public SubscribeForMsgUpdatesRs subscribe(RocketWebsocketClient websocketClient, String rocketchatUserId) {
        SubscribeForMsgUpdatesRq subscribeRequest = commonServiceModule.getWebsocketClientMapper().generateSubscribeForMsgUpdatesRequest(rocketchatUserId);

        websocketClient.sendSubscribeForMessagesUpdatesRequest(subscribeRequest);
        String response = websocketClient.waitForMessage();

        return commonServiceModule.getSubscribeForMsgUpdates(response);
    }

    public void sendQrCommand(RocketWebsocketClient websocketClient, String cmd, String roomId) {
        SendCommandRq sendCommandRequest = commonServiceModule.getWebsocketClientMapper().generateSendCommandRequest(cmd, roomId);
        websocketClient.sendCommandRequest(sendCommandRequest);
    }

    @Nullable
    public QrCodeMsg waitForQrCode(RocketWebsocketClient websocketClient) {
        for (int i = 0; i < 2; i++) {
            String response = websocketClient.waitForMessage();
            if (response == null) {
                i--;
                continue;
            }

            MessageChangedNotificationRs messageChangedResponse = commonServiceModule.getMessageChangedNotification(response);
            if (messageChangedResponse != null && messageChangedResponse.getError() != null) {
                return QrCodeMsg
                        .builder()
                        .text(messageChangedResponse.getError().getMessage())
                        .build();
            }

            try {
                QrCodeMsg msg = new QrCodeMsg();
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
                               msg.setText(value.getValueString());
                            } else if (value.getType().equals("IMAGE")) {
                               msg.setImage(value.getValueObj().getSrc().getValue());
                            }
                        });
                if (msg.getImage() != null || msg.getText() != null) {
                    return msg;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QrCodeMsg {
        private String text;
        private String image;
    }
}
