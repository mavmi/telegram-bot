package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.authMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.database.RocketchatDatabaseService;
import mavmi.telegram_bot.rocketchat.service.database.dto.RocketchatDto;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth.VerifyCredsWebsocketClient;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class AuthMenuHandler extends MenuRequestHandler<RocketchatServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final PmsUtils pmsUtils;
    private final RocketchatDatabaseService databaseService;

    public AuthMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils,
                           TelegramBotUtils telegramBotUtils,
                           PmsUtils pmsUtils,
                           RocketchatDatabaseService databaseService) {
        super(menuEngine, RocketMenu.AUTH);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
        this.pmsUtils = pmsUtils;
        this.databaseService = databaseService;
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
        RocketchatDto dto = databaseService.findByTelegramId(chatIt);

        if (dto != null) {
            CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
            RocketDataCache dataCache = commonUtils.getUserCaches().getDataCache(RocketDataCache.class);
            TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
            RocketchatDto decryptedDto = cryptoMapper.decryptRocketchatDto(textEncryptor, dto);

            dataCache.setRocketchatUsername(decryptedDto.getRocketchatUsername());
            dataCache.setRocketchatPasswordHash(decryptedDto.getRocketchatPasswordHash());

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
