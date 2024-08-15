package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.SocketCommunicationServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketchatWebsocketClient;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketchatWebsocketClientBuilder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatRepository rocketchatRepository;
    private final RocketchatServiceConstants constants;
    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;
    private final SocketCommunicationServiceModule socketCommunicationServiceModule;
    private final RocketchatWebsocketClientBuilder websocketClientBuilder;
    private final UserAuthentication userAuthentication;

    public AuthServiceModule(
            RocketchatRepository rocketchatRepository,
            RocketchatServiceConstantsHandler constantsHandler,
            CommonServiceModule commonServiceModule,
            SocketCommunicationServiceModule socketCommunicationServiceModule,
            RocketchatWebsocketClientBuilder websocketClientBuilder,
            UserAuthentication userAuthentication) {
        List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methodsOnAuth = List.of(this::onAuth);
        List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methodsOnDefault = List.of(this::onDefault);

        this.rocketchatRepository = rocketchatRepository;
        this.constants = constantsHandler.get();
        this.rocketchatChainServiceMessageToServiceSecondaryMethodsContainer = new RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(
                Map.of(
                        constants.getRequests().getStart(), methodsOnAuth,
                        constants.getRequests().getAuth(), methodsOnAuth
                ),
                methodsOnDefault
        );
        this.commonServiceModule = commonServiceModule;
        this.socketCommunicationServiceModule = socketCommunicationServiceModule;
        this.websocketClientBuilder = websocketClientBuilder;
        this.userAuthentication = userAuthentication;
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServiceSecondaryMethodsContainer.getMethods(msg);
    }

    public RocketchatServiceRs onAuth(RocketchatServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketchatServiceDataCache.class).getMenuContainer().add(RocketchatServiceMenu.AUTH);
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getAskForRocketchatCreds());
    }

    public RocketchatServiceRs onDefault(RocketchatServiceRq request) {
        commonServiceModule.dropMenu();

        CryptoMapper cryptoMapper = commonServiceModule.getCryptoMapper();
        TextEncryptor textEncryptor = commonServiceModule.getTextEncryptor();
        long chatId = request.getChatId();
        String[] arr = Utils.splitByFirstSpace(request.getMessageJson().getTextMessage());
        if (arr == null) {
            return commonServiceModule.createBadRequestResponse();
        }

        String rocketchatUsername = arr[0];
        String rocketchatUsernamePasswordHash = Utils.calculateHash(arr[1]);

        LoginRs loginResponse = verifyCreds(rocketchatUsername, rocketchatUsernamePasswordHash);
        if (loginResponse == null || loginResponse.getError() != null) {
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getInvalidCreds());
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
            model = cryptoMapper.decryptRocketchatModel(textEncryptor, model);
            model.setRocketchatUsername(rocketchatUsername);
            model.setRocketchatPasswordHash(rocketchatUsernamePasswordHash);
            model.setRocketchatToken(rocketchatToken);
            model.setRocketchatTokenExpiryDate(rocketchatTokenExpiry);
            model = cryptoMapper.encryptRocketchatModel(textEncryptor, model);

            rocketchatRepository.updateByTelegramId(
                    model.getTelegramId(),
                    model.getTelegramUsername(),
                    model.getTelegramFirstname(),
                    model.getTelegramLastname(),
                    model.getRocketchatUsername(),
                    model.getRocketchatPasswordHash(),
                    model.getRocketchatToken(),
                    model.getRocketchatTokenExpiryDate()
            );
        }

        return commonServiceModule.createSendTextResponse(constants.getPhrases().getOk());
    }

    private LoginRs verifyCreds(String rocketchatUsername, String rocketchatUsernamePasswordHash) {
        RocketchatWebsocketClient websocketClient = websocketClientBuilder.getWebsocketClient();
        if (socketCommunicationServiceModule.connect(websocketClient) == null) {
            websocketClient.close();
            return null;
        }

        LoginRs loginResponse = socketCommunicationServiceModule.login(websocketClient, rocketchatUsername, rocketchatUsernamePasswordHash);
        websocketClient.close();
        return loginResponse;
    }
}
