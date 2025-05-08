package mavmi.telegram_bot.rocketchat.service.menuHandlers.authMenu;

import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.lib.database_starter.repository.RocketchatRepository;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.auth.AuthServiceWebsocketMessageHandler;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import mavmi.telegram_bot.rocketchat.websocket.api.messageHandler.OnResult;
import mavmi.telegram_bot.rocketchat.websocket.impl.client.RocketWebsocketClient;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthMenuHandler extends MenuRequestHandler<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final PmsUtils pmsUtils;

    public AuthMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils,
                           TelegramBotUtils telegramBotUtils,
                           PmsUtils pmsUtils) {
        super(menuEngine, RocketMenu.AUTH);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
        this.pmsUtils = pmsUtils;
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        init(request);
        auth(request);
        deleteIncomingMessage(request);
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonUtils.getUserCaches().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    public void auth(RocketchatServiceRq request) {
        long chatIt = request.getChatId();
        RocketchatRepository repository = commonUtils.getRocketchatRepository();
        Optional<RocketchatModel> optional = repository.findByTelegramId(chatIt);
        OnResult<RocketchatServiceRq> onBadCredentials = (req, payload) -> {
            UserCaches userCaches = (UserCaches) payload[1];

            userCaches.getDataCache(RocketDataCache.class).getMenuHistoryContainer().add(RocketMenu.AUTH_ENTER_LOGIN);
            int msgId = telegramBotUtils.sendText(req.getChatId(), commonUtils.getConstants().getPhrases().getAuth().getEnterLogin());
            commonUtils.addMessageToDeleteAfterEnd(msgId, userCaches);
        };

        if (optional.isPresent()) {
            CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
            Creds creds = commonUtils.getUserCaches().getDataCache(RocketDataCache.class).getCreds();
            TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
            RocketchatModel rocketchatModel = cryptoMapper.decryptRocketchatModel(textEncryptor, optional.get());

            creds.setRocketchatUsername(rocketchatModel.getRocketchatUsername());
            creds.setRocketchatPasswordHash(rocketchatModel.getRocketchatPasswordHash());

            AuthServiceWebsocketMessageHandler messageHandler = new AuthServiceWebsocketMessageHandler(commonUtils, telegramBotUtils, pmsUtils);
            RocketWebsocketClient websocketClient = RocketWebsocketClient.build(
                    request.getChatId(),
                    commonUtils.getRocketchatUrl(),
                    messageHandler,
                    pmsUtils.getConnectionTimeout(),
                    pmsUtils.getAwaitingPeriodMillis(),
                    commonUtils
            );
            messageHandler.start(
                    commonUtils.getUserCaches(),
                    request,
                    websocketClient,
                    (req, payload) -> {
                        UserCaches userCaches = (UserCaches) payload[1];

                        long chatId = req.getChatId();
                        int msgId = telegramBotUtils.sendText(chatId, commonUtils.getConstants().getPhrases().getAuth().getAlreadyLoggedIn());
                        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
                        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
                    },
                    onBadCredentials
            );
        } else {
            onBadCredentials.process(request, null, commonUtils.getUserCaches());
        }
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }
}
