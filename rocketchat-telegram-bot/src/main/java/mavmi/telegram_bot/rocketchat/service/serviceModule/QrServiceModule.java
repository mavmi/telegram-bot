package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.httpClient.RocketchatHttpClient;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.mapper.WebsocketClientMapper;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
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

@Component
public class QrServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private static final int MAX_ATTEMPTS = 5;

    private final WebsocketClientMapper websocketClientMapper;
    private final RocketchatRepository rocketchatRepository;
    private final RocketchatServiceConstants constants;
    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;
    private final SocketCommunicationServiceModule socketCommunicationServiceModule;

    public QrServiceModule(
            WebsocketClientMapper websocketClientMapper,
            RocketchatRepository rocketchatRepository,
            RocketchatServiceConstantsHandler constantsHandler,
            CommonServiceModule commonServiceModule,
            SocketCommunicationServiceModule socketCommunicationServiceModule
    ) {
        List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methodsOnDefault = List.of(
                this::inform,
                this::onDefault
        );

        this.websocketClientMapper = websocketClientMapper;
        this.rocketchatRepository = rocketchatRepository;
        this.constants = constantsHandler.get();
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
        Optional<RocketchatModel> modelOptional = rocketchatRepository.findByTelegramId(chatId);
        CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
        TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
        if (modelOptional.isEmpty()) {
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getCredsNotFound());
        }

        RocketchatModel model = modelOptional.get();
        model = cryptoMapper.decryptRocketchatModel(textEncryptor, model);
        RocketchatWebsocketClient websocketClient = commonServiceModule.getWebsocketClientBuilder().getWebsocketClient();

        ConnectRs connectResponse = socketCommunicationServiceModule.connect(websocketClient);
        if (connectResponse == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        }

        LoginRs loginResponse = socketCommunicationServiceModule.login(websocketClient, model.getRocketchatUsername(), model.getRocketchatPasswordHash());
        if (loginResponse == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        } else if (loginResponse.getError() != null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(
                    constants.getPhrases().getError() +
                            "\n" +
                            loginResponse.getError().getMessage()
            );
        }

        CreateDMRs createDMResponse = socketCommunicationServiceModule.createRoom(websocketClient, model.getRocketchatUsername());
        if (createDMResponse == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        }

        String rocketchatUserId = loginResponse.getResult().getId();
        String rocketchatUserToken = loginResponse.getResult().getToken();
        SubscribeForMsgUpdatesRs subscribeResponse = socketCommunicationServiceModule.subscribe(websocketClient, rocketchatUserId);
        if (subscribeResponse == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        }

        String roomId = createDMResponse.getResult().getRid();
        SendCommandRs sendCommandResponse = sendQrCommand(rocketchatUserId, rocketchatUserToken, roomId);
        if (sendCommandResponse == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        } else if (!sendCommandResponse.isSuccess()) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(
                    constants.getPhrases().getError() +
                            "\n" +
                            sendCommandResponse.getError()
            );
        }

        SocketCommunicationServiceModule.QrCodeMsg qrCodeMsg = socketCommunicationServiceModule.waitForQrCode(websocketClient);
        if (qrCodeMsg == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        } else if (qrCodeMsg.getImage() == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(qrCodeMsg.getText());
        }

        File qrCodeFile = createQrFile(qrCodeMsg.getImage());
        if (qrCodeFile == null) {
            websocketClient.close();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getError());
        }

        websocketClient.close();
        return commonServiceModule.createSendImageResponse(qrCodeMsg.getText(), qrCodeFile.getAbsolutePath());
    }

    private RocketchatServiceRs inform(RocketchatServiceRq request) {
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getQrIsCreatingResponse());
    }

    @Nullable
    private SendCommandRs sendQrCommand(String rocketchatUserId, String rocketchatUserToken, String roomId) {
        RocketchatHttpClient httpClient = commonServiceModule.getRocketchatHttpClient();
        return httpClient.sendQrCommand(rocketchatUserId, rocketchatUserToken, roomId);
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
                e.printStackTrace(System.out);
            }
        }

        return null;
    }
}
