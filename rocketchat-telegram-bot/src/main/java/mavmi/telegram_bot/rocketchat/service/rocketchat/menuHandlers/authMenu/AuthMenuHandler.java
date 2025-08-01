package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.authMenu;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
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
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth.AUTH_MODE;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth.VerifyCredsWebsocketClient;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.List;

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
        long chatId = request.getChatId();
        String msg = request.getMessageJson().getTextMessage();
        AUTH_MODE authMode = getAuthMode(request);

        init(request);
        deleteIncomingMessage(request);

        if (msg.equals(commonUtils.getConstants().getRequests().getAuth())) {
            commonUtils.getUserCaches().getDataCache().getMenuHistoryContainer().add(RocketMenu.AUTH);
            sendMenuButtons(chatId);
        } else if (authMode != null) {
            verifyExistingCreds(request, authMode);
        } else {
            sendMenuButtons(chatId);
        }
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonUtils.getUserCaches().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    public void verifyExistingCreds(RocketchatServiceRq request, AUTH_MODE authMode) {
        long chatId = request.getChatId();
        CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
        TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
        RocketchatDto encryptedDto = databaseService.findByTelegramId(chatId);
        RocketchatDto decryptedDto = (encryptedDto == null) ? null : cryptoMapper.decryptRocketchatDto(textEncryptor, encryptedDto);

        if (decryptedDto == null ||
                decryptedDto.getRocketchatToken() == null && authMode == AUTH_MODE.TOKEN ||
                decryptedDto.getRocketchatPasswordHash() == null && authMode == AUTH_MODE.PASSWORD) {
            Menu nextMenu = (authMode == AUTH_MODE.TOKEN) ? RocketMenu.AUTH_ENTER_TOKEN : RocketMenu.AUTH_ENTER_LOGIN;
            String msgToSend = (authMode == AUTH_MODE.TOKEN) ? commonUtils.getConstants().getPhrases().getAuth().getEnterToken() :
                    commonUtils.getConstants().getPhrases().getAuth().getEnterLogin();

            commonUtils.getUserCaches()
                    .getDataCache(RocketDataCache.class)
                    .getMenuHistoryContainer()
                    .add(nextMenu);
            int msgId = telegramBotUtils.sendText(chatId, msgToSend);
            commonUtils.addMessageToDeleteAfterEnd(msgId);
        } else {
            RocketDataCache dataCache = commonUtils.getUserCaches().getDataCache(RocketDataCache.class);
            dataCache.setRocketchatUsername(decryptedDto.getRocketchatUsername());
            dataCache.setRocketchatPasswordHash(decryptedDto.getRocketchatPasswordHash());
            dataCache.setRocketchatToken(decryptedDto.getRocketchatToken());

            VerifyCredsWebsocketClient websocketClient = new VerifyCredsWebsocketClient(request,
                    commonUtils.getUserCaches(),
                    commonUtils,
                    telegramBotUtils,
                    pmsUtils,
                    authMode);
            websocketClient.start();
        }
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonUtils.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void sendMenuButtons(long chatId) {
        List<String> buttons = menuEngine.getMenuButtonsAsString(RocketMenu.AUTH);
        int msgId = telegramBotUtils.sendReplyKeyboard(chatId,
                commonUtils.getConstants().getPhrases().getAuth().getSelectMethod(),
                buttons);
        commonUtils.addMessageToDeleteAfterEnd(msgId);
    }

    @Nullable
    private AUTH_MODE getAuthMode(RocketchatServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        if (msg.equals(menuEngine.getMenuButtonByName(RocketMenu.AUTH, "using_token").getValue())) {
            return AUTH_MODE.TOKEN;
        } else if (msg.equals(menuEngine.getMenuButtonByName(RocketMenu.AUTH, "using_password").getValue())) {
            return AUTH_MODE.PASSWORD;
        } else {
            return null;
        }
    }
}
