package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.ConnectRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.CreateDMRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.SubscribeForMsgUpdatesRs;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.SocketCommunicationServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketWebsocketClient;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class QrServiceModule implements ServiceModule<RocketchatServiceRq> {

    private static final int MAX_ATTEMPTS = 5;

    private final CommonServiceModule commonServiceModule;
    private final SocketCommunicationServiceModule socketCommunicationServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public QrServiceModule(
            CommonServiceModule commonServiceModule,
            SocketCommunicationServiceModule socketCommunicationServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.socketCommunicationServiceModule = socketCommunicationServiceModule;
        this.serviceComponentsContainer.setDefaultServiceMethods(List.of(this::init, this::deleteIncomingMessage, this::inform, this::onDefault));
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getDefaultServiceMethods()) {
            method.process(request);
        }
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    private void onDefault(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        Optional<RocketchatModel> modelOptional = commonServiceModule.getRocketchatRepository().findByTelegramId(chatId);
        RocketConstants constants = commonServiceModule.getConstants();
        CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
        TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
        if (modelOptional.isEmpty()) {
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getCredsNotFound());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        }

        RocketchatModel model = modelOptional.get();
        model = cryptoMapper.decryptRocketchatModel(textEncryptor, model);
        RocketWebsocketClient websocketClient = commonServiceModule.getWebsocketClientBuilder().getWebsocketClient();

        ConnectRs connectResponse = socketCommunicationServiceModule.connect(websocketClient);
        if (connectResponse == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        }

        LoginRs loginResponse = socketCommunicationServiceModule.login(websocketClient, model.getRocketchatUsername(), model.getRocketchatPasswordHash());
        if (loginResponse == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        } else if (loginResponse.getError() != null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError() + "\n" + loginResponse.getError().getMessage());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        }

        CreateDMRs createDMResponse = socketCommunicationServiceModule.createRoom(websocketClient, model.getRocketchatUsername());
        if (createDMResponse == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
        }

        String rocketchatUserId = loginResponse.getResult().getId();
        SubscribeForMsgUpdatesRs subscribeResponse = socketCommunicationServiceModule.subscribe(websocketClient, rocketchatUserId);
        if (subscribeResponse == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
        }

        String roomId = createDMResponse.getResult().getRid();
        socketCommunicationServiceModule.sendQrCommand(websocketClient, commonServiceModule.getQrCommand(), roomId);

        SocketCommunicationServiceModule.QrCodeMsg qrCodeMsg = socketCommunicationServiceModule.waitForQrCode(websocketClient);
        if (qrCodeMsg == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        } else if (qrCodeMsg.getImage() == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, qrCodeMsg.getText());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        }

        File qrCodeFile = createQrFile(qrCodeMsg.getImage());
        if (qrCodeFile == null) {
            websocketClient.close();
            int msgId = commonServiceModule.sendText(chatId, constants.getPhrases().getError());
            commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
            commonServiceModule.deleteMsgs(chatId);
            return;
        }

        websocketClient.close();
        int msgId = commonServiceModule.sendImage(chatId, qrCodeMsg.getText(), new File(qrCodeFile.getAbsolutePath()));
        commonServiceModule.deleteAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisQr());
        commonServiceModule.deleteMsgs(chatId);
    }

    private void inform(RocketchatServiceRq request) {
        int msgId = commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getQrIsCreatingResponse());
        commonServiceModule.addMsgToDeleteAfterEnd(msgId);
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMsgToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    @Nullable
    private File createQrFile(String base64qrCode) {
        String base64file = base64qrCode.split(",")[1];
        byte[] fileBytes = DatatypeConverter.parseBase64Binary(base64file);
        String randomFileName = Utils.generateRandomString() + ".png";
        File file = new File(commonServiceModule.getOutputDirectoryPath() + "/" + randomFileName);

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(fileBytes);
                return file;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }
}
