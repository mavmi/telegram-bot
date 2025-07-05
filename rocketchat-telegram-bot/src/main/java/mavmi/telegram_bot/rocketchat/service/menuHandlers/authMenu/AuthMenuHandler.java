package mavmi.telegram_bot.rocketchat.service.menuHandlers.authMenu;

import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.lib.database_starter.repository.RocketchatRepository;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.auth.VerifyCredsWebsocketClient;
import mavmi.telegram_bot.rocketchat.utils.Utils;
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

        if (optional.isPresent()) {
            CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
            RocketDataCache dataCache = commonUtils.getUserCaches().getDataCache(RocketDataCache.class);
            TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
            RocketchatModel rocketchatModel = cryptoMapper.decryptRocketchatModel(textEncryptor, optional.get());

            dataCache.setRocketchatUsername(rocketchatModel.getRocketchatUsername());
            dataCache.setRocketchatPasswordHash(rocketchatModel.getRocketchatPasswordHash());

            VerifyCredsWebsocketClient websocketClient = new VerifyCredsWebsocketClient(request,
                    commonUtils.getUserCaches(),
                    commonUtils,
                    telegramBotUtils,
                    pmsUtils);
            websocketClient.start();
        } else {
            commonUtils.getUserCaches()
                    .getDataCache(RocketDataCache.class)
                    .getMenuHistoryContainer()
                    .add(RocketMenu.AUTH_ENTER_LOGIN);
            int msgId = telegramBotUtils.sendText(request.getChatId(),
                    commonUtils.getConstants().getPhrases().getAuth().getEnterLogin());
            commonUtils.addMessageToDeleteAfterEnd(msgId);
        }
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }
}
