package mavmi.telegram_bot.rocketchat.service.menuHandlers.authEnterPasswordMenu;

import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.lib.database_starter.repository.RocketchatRepository;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.auth.AuthServiceWebsocketMessageHandler;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthEnterPasswordHandler extends MenuRequestHandler<RocketchatServiceRq> {

    private final MenuEngine menuEngine;
    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public AuthEnterPasswordHandler(MenuEngine menuEngine,
                                    CommonUtils commonUtils,
                                    TelegramBotUtils telegramBotUtils) {
        super(menuEngine, RocketMenu.AUTH_ENTER_PASSWORD);
        this.menuEngine = menuEngine;
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        getPassword(request);
        deleteIncomingMessage(request);
    }

    private void getPassword(RocketchatServiceRq request) {
        String password = request.getMessageJson().getTextMessage();
        RocketDataCache dataCache = commonUtils.getUserCaches().getDataCache(RocketDataCache.class);

        dataCache.getCreds().setRocketchatPasswordHash(Utils.calculateHash(password));

        doLogin(request);
        commonUtils.dropUserMenu();
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
        telegramBotUtils.deleteQueuedMessages(request.getChatId(), commonUtils.getUserCaches());
    }

    private void doLogin(RocketchatServiceRq request) {
        AuthServiceWebsocketMessageHandler messageHandler = new AuthServiceWebsocketMessageHandler(commonUtils, telegramBotUtils);
        RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                request.getChatId(),
                commonUtils.getRocketchatUrl(),
                messageHandler,
                commonUtils.getConnectionTimeout(),
                commonUtils.getAwaitingPeriodMillis(),
                commonUtils
        );
        messageHandler.start(
                commonUtils.getUserCaches(),
                request,
                websocketClient,
                (req, payload) -> {
                    LoginRs loginResponse = (LoginRs) payload[0];
                    UserCaches userCaches = (UserCaches) payload[1];
                    RocketchatRepository rocketchatRepository = commonUtils.getRocketchatRepository();
                    CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
                    TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
                    Creds creds = userCaches.getDataCache(RocketDataCache.class).getCreds();

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

                    telegramBotUtils.sendText(chatId, commonUtils.getConstants().getPhrases().getAuth().getAuthSuccess() + ": " + rocketchatUsername);
                },
                (req, payload) -> {
                    long chatId = req.getChatId();
                    String textMsg = (String) payload[0];
                    UserCaches userCaches = (UserCaches) payload[1];

                    int msgId = telegramBotUtils.sendText(chatId, textMsg);
                    telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, commonUtils.getDeleteAfterMillisNotification());
                    telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
                }
        );
    }
}
