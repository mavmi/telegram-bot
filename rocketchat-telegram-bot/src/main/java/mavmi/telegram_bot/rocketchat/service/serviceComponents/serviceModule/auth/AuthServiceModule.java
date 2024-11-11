package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketConstantsHandler;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.SocketCommunicationServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketWebsocketClient;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketWebsocketClientBuilder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AuthServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final SocketCommunicationServiceModule socketCommunicationServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public AuthServiceModule(
            RocketchatRepository rocketchatRepository,
            RocketConstantsHandler constantsHandler,
            CommonServiceModule commonServiceModule,
            SocketCommunicationServiceModule socketCommunicationServiceModule,
            RocketWebsocketClientBuilder websocketClientBuilder
    ) {
        List<ServiceMethod<RocketchatServiceRq>> methodsOnAuth = List.of(this::init, this::onAuth, this::deleteIncomingMessage);

        this.commonServiceModule = commonServiceModule;
        this.socketCommunicationServiceModule = socketCommunicationServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getStart(), methodsOnAuth)
                .add(commonServiceModule.getConstants().getRequests().getAuth(), methodsOnAuth);
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getMethods(msg)) {
            method.process(request);
        }
    }

    public void onAuth(RocketchatServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).getMenuContainer().add(RocketMenu.AUTH_ENTER_LOGIN);
        int msgId = commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getEnterLogin());
        commonServiceModule.addMsgToDeleteAfterEnd(msgId);
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMsgToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    public MessageJson doLogin(RocketchatServiceRq request) {
        commonServiceModule.dropMenu();

        RocketchatRepository rocketchatRepository = commonServiceModule.getRocketchatRepository();
        Creds creds = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).getCreds();
        CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
        TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
        long chatId = request.getChatId();

        String rocketchatUsername = creds.getUsername();
        String rocketchatUsernamePasswordHash = Utils.calculateHash(creds.getPassword());

        LoginRs loginResponse = verifyCreds(rocketchatUsername, rocketchatUsernamePasswordHash);
        if (loginResponse == null || loginResponse.getError() != null) {
            return MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidCreds() + ": " + rocketchatUsername)
                    .build();
        }

        String rocketchatToken = loginResponse.getResult().getToken();
        Long rocketchatTokenExpiry = loginResponse.getResult().getTokenExpires().getDate();
        Optional<RocketchatModel> modelOptional = rocketchatRepository.findByTelegramId(chatId);

        if (modelOptional.isEmpty()) {
            RocketchatModel model = RocketchatModel
                    .builder()
                    .telegramId(chatId)
                    .telegramUsername(request.getUserJson().getUsername())
                    .telegramFirstname(request.getUserJson().getFirstName())
                    .telegramLastname(request.getUserJson().getLastName())
                    .rocketchatUsername(rocketchatUsername)
                    .rocketchatPasswordHash(rocketchatUsernamePasswordHash)
                    .rocketchatToken(rocketchatToken)
                    .rocketchatTokenExpiryDate(rocketchatTokenExpiry)
                    .build();
            model = cryptoMapper.encryptRocketchatModel(textEncryptor, model);
            rocketchatRepository.save(model);
        } else {
            RocketchatModel model = modelOptional.get();
            model = cryptoMapper.decryptRocketchatModel(textEncryptor, model)
                    .setRocketchatUsername(rocketchatUsername)
                    .setRocketchatPasswordHash(rocketchatUsernamePasswordHash)
                    .setRocketchatToken(rocketchatToken)
                    .setRocketchatTokenExpiryDate(rocketchatTokenExpiry);
            model = cryptoMapper.encryptRocketchatModel(textEncryptor, model);

            rocketchatRepository.updateByTelegramId(model);
        }

        return MessageJson
                .builder()
                .textMessage(commonServiceModule.getConstants().getPhrases().getAuthSuccess() + ": " + rocketchatUsername)
                .build();
    }

    private LoginRs verifyCreds(String rocketchatUsername, String rocketchatUsernamePasswordHash) {
        RocketWebsocketClient websocketClient = commonServiceModule.getWebsocketClientBuilder().getWebsocketClient();
        if (socketCommunicationServiceModule.connect(websocketClient) == null) {
            websocketClient.close();
            return null;
        }

        LoginRs loginResponse = socketCommunicationServiceModule.login(websocketClient, rocketchatUsername, rocketchatUsernamePasswordHash);
        websocketClient.close();
        return loginResponse;
    }
}
