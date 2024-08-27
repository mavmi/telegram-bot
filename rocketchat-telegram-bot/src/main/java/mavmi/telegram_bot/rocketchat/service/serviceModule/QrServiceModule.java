package mavmi.telegram_bot.rocketchat.service.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.ConnectRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.CreateDMRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.SubscribeForMsgUpdatesRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.SocketCommunicationServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketchatWebsocketClient;
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
public class QrServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private static final int MAX_ATTEMPTS = 5;

    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;
    private final SocketCommunicationServiceModule socketCommunicationServiceModule;

    public QrServiceModule(
            CommonServiceModule commonServiceModule,
            SocketCommunicationServiceModule socketCommunicationServiceModule
    ) {
        List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methodsOnDefault = List.of(
                this::deleteIncomingMessage,
                this::inform,
                this::onDefault
        );

        this.rocketchatChainServiceMessageToServiceSecondaryMethodsContainer = new RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(
                methodsOnDefault
        );
        this.commonServiceModule = commonServiceModule;
        this.socketCommunicationServiceModule = socketCommunicationServiceModule;
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServiceSecondaryMethodsContainer.getMethods(msg);
    }

    private RocketchatServiceRs onDefault(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        Optional<RocketchatModel> modelOptional = commonServiceModule.getRocketchatRepository().findByTelegramId(chatId);
        RocketchatServiceConstants constants = commonServiceModule.getConstants();
        CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
        TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
        if (modelOptional.isEmpty()) {
            return commonServiceModule.createResponse(constants.getPhrases().getCredsNotFound(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        }

        RocketchatModel model = modelOptional.get();
        model = cryptoMapper.decryptRocketchatModel(textEncryptor, model);
        RocketchatWebsocketClient websocketClient = commonServiceModule.getWebsocketClientBuilder().getWebsocketClient();

        ConnectRs connectResponse = socketCommunicationServiceModule.connect(websocketClient);
        if (connectResponse == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(constants.getPhrases().getError(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        }

        LoginRs loginResponse = socketCommunicationServiceModule.login(websocketClient, model.getRocketchatUsername(), model.getRocketchatPasswordHash());
        if (loginResponse == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(constants.getPhrases().getError(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        } else if (loginResponse.getError() != null) {
            websocketClient.close();
            return commonServiceModule.createResponse(
                    constants.getPhrases().getError() +
                            "\n" +
                            loginResponse.getError().getMessage(),
                    null,
                    null,
                    commonServiceModule.getDeleteAfterMillis(),
                    List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END)
            );
        }

        CreateDMRs createDMResponse = socketCommunicationServiceModule.createRoom(websocketClient, model.getRocketchatUsername());
        if (createDMResponse == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(constants.getPhrases().getError(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        }

        String rocketchatUserId = loginResponse.getResult().getId();
        SubscribeForMsgUpdatesRs subscribeResponse = socketCommunicationServiceModule.subscribe(websocketClient, rocketchatUserId);
        if (subscribeResponse == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(constants.getPhrases().getError(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        }

        String roomId = createDMResponse.getResult().getRid();
        socketCommunicationServiceModule.sendQrCommand(websocketClient, commonServiceModule.getQrCommand(), roomId);

        SocketCommunicationServiceModule.QrCodeMsg qrCodeMsg = socketCommunicationServiceModule.waitForQrCode(websocketClient);
        if (qrCodeMsg == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(constants.getPhrases().getError(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        } else if (qrCodeMsg.getImage() == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(qrCodeMsg.getText(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        }

        File qrCodeFile = createQrFile(qrCodeMsg.getImage());
        if (qrCodeFile == null) {
            websocketClient.close();
            return commonServiceModule.createResponse(constants.getPhrases().getError(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
        }

        websocketClient.close();
        return commonServiceModule.createResponse(
                qrCodeMsg.getText(),
                qrCodeFile.getAbsolutePath(),
                null,
                commonServiceModule.getDeleteAfterMillis(),
                List.of(ROCKETCHAT_SERVICE_TASK.SEND_IMAGE, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END)
        );
    }

    private RocketchatServiceRs inform(RocketchatServiceRq request) {
        return commonServiceModule.createResponse(
                commonServiceModule.getConstants().getPhrases().getQrIsCreatingResponse(),
                null,
                null,
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_END)
        );
    }

    private RocketchatServiceRs deleteIncomingMessage(RocketchatServiceRq request) {
        return commonServiceModule.createResponse(
                null,
                null,
                request.getMessageJson().getMsgId(),
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_END)
        );
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
