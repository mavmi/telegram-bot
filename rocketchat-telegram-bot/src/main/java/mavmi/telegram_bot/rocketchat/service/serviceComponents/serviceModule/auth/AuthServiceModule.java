package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.messageHandler.AuthServiceWebsocketMessageHandler;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.OnResult;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AuthServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public AuthServiceModule(CommonServiceModule commonServiceModule) {
        List<ServiceMethod<RocketchatServiceRq>> methodsOnAuth = List.of(this::init, this::onAuth, this::deleteIncomingMessage);

        this.commonServiceModule = commonServiceModule;
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

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    public void onAuth(RocketchatServiceRq request) {
        long chatIt = request.getChatId();
        RocketchatRepository repository = commonServiceModule.getRocketchatRepository();
        Optional<RocketchatModel> optional = repository.findByTelegramId(chatIt);
        OnResult<RocketchatServiceRq> onBadCredentials = (req, payload) -> {
            commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).setMenu(RocketMenu.AUTH_ENTER_LOGIN);
            int msgId = commonServiceModule.sendText(req.getChatId(), commonServiceModule.getConstants().getPhrases().getAuth().getEnterLogin());
            commonServiceModule.addMessageToDeleteAfterEnd(msgId);
        };

        if (optional.isPresent()) {
            CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
            Creds creds = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).getCreds();
            TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
            RocketchatModel rocketchatModel = cryptoMapper.decryptRocketchatModel(textEncryptor, optional.get());

            creds.setRocketchatUsername(rocketchatModel.getRocketchatUsername());
            creds.setRocketchatPasswordHash(rocketchatModel.getRocketchatPasswordHash());

            AuthServiceWebsocketMessageHandler messageHandler = new AuthServiceWebsocketMessageHandler(commonServiceModule);
            RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                    request.getChatId(),
                    commonServiceModule.getRocketchatUrl(),
                    messageHandler,
                    commonServiceModule.getConnectionTimeout(),
                    commonServiceModule.getAwaitingPeriodMillis(),
                    commonServiceModule
            );
            messageHandler.start(
                    request,
                    websocketClient,
                    (req, payload) -> {
                        long chatId = req.getChatId();
                        int msgId = commonServiceModule.sendText(chatId, commonServiceModule.getConstants().getPhrases().getAuth().getAlreadyLoggedIn());
                        commonServiceModule.deleteMessageAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
                        commonServiceModule.deleteQueuedMessages(chatId);
                    },
                    onBadCredentials
            );
        } else {
            onBadCredentials.process(request);
        }
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    public void doLogin(RocketchatServiceRq request) {
        AuthServiceWebsocketMessageHandler messageHandler = new AuthServiceWebsocketMessageHandler(commonServiceModule);
        RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                request.getChatId(),
                commonServiceModule.getRocketchatUrl(),
                messageHandler,
                commonServiceModule.getConnectionTimeout(),
                commonServiceModule.getAwaitingPeriodMillis(),
                commonServiceModule
        );
        messageHandler.start(
                request,
                websocketClient,
                (req, payload) -> {
                    LoginRs loginResponse = (LoginRs) payload[0];
                    RocketchatRepository rocketchatRepository = commonServiceModule.getRocketchatRepository();
                    CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
                    TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
                    Creds creds = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class).getCreds();

                    long chatId = req.getChatId();
                    String rocketchatUsername = creds.getRocketchatUsername();
                    String rocketchatPasswordHash = creds.getRocketchatPasswordHash();
                    String rocketchatToken = loginResponse.getResult().getToken();
                    Long rocketchatTokenExpiry = loginResponse.getResult().getTokenExpires().getDate();
                    Optional<RocketchatModel> optional = rocketchatRepository.findByTelegramId(chatId);

                    if (optional.isEmpty()) {
                        RocketchatModel rocketchatModel = RocketchatModel
                                .builder()
                                .telegramId(chatId)
                                .telegramUsername(request.getUserJson().getUsername())
                                .telegramFirstname(request.getUserJson().getFirstName())
                                .telegramLastname(request.getUserJson().getLastName())
                                .rocketchatUsername(rocketchatUsername)
                                .rocketchatPasswordHash(rocketchatPasswordHash)
                                .rocketchatToken(rocketchatToken)
                                .rocketchatTokenExpiryDate(rocketchatTokenExpiry)
                                .build();
                        rocketchatModel = cryptoMapper.encryptRocketchatModel(textEncryptor, rocketchatModel);
                        rocketchatRepository.save(rocketchatModel);
                    } else {
                        RocketchatModel model = optional.get();
                        model = cryptoMapper.decryptRocketchatModel(textEncryptor, model)
                                .setRocketchatUsername(rocketchatUsername)
                                .setRocketchatPasswordHash(rocketchatPasswordHash)
                                .setRocketchatToken(rocketchatToken)
                                .setRocketchatTokenExpiryDate(rocketchatTokenExpiry);
                        model = cryptoMapper.encryptRocketchatModel(textEncryptor, model);

                        rocketchatRepository.updateByTelegramId(model);
                    }

                    commonServiceModule.sendText(chatId, commonServiceModule.getConstants().getPhrases().getAuth().getAuthSuccess() + ": " + rocketchatUsername);
                },
                (req, payload) -> {
                    long chatId = req.getChatId();
                    String textMsg = (String) payload[0];
                    int msgId = commonServiceModule.sendText(chatId, textMsg);
                    commonServiceModule.deleteMessageAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
                    commonServiceModule.deleteQueuedMessages(chatId);
                }
        );
    }
}
