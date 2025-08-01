package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.mapper.WebsocketClientMapper;
import mavmi.telegram_bot.rocketchat.service.database.RocketchatDatabaseService;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.telegramBot.client.RocketTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class CommonUtils {

    private final UserCachesProvider userCachesProvider;
    private final RocketTelegramBotSender sender;
    private final CryptoMapper cryptoMapper;
    private final WebsocketClientMapper websocketClientMapper;
    private final RocketchatDatabaseService databaseService;

    private RocketConstants constants;
    private TextEncryptor textEncryptor;
    private String outputDirectoryPath;
    private String qrCommand;
    private String rocketchatUrl;

    @Autowired
    public void setup(@Qualifier("rocketChatTextEncryptor") TextEncryptor textEncryptor,
                      @Value("${service.output-directory}") String outputDirectoryPath,
                      @Value("${service.commands.commands-list.qr}") String qrCommand,
                      @Value("${websocket.client.url}") String rocketchatUrl,
                      RocketConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
        this.textEncryptor = textEncryptor;
        this.outputDirectoryPath = outputDirectoryPath;
        this.qrCommand = qrCommand;
        this.rocketchatUrl = rocketchatUrl;
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public void addMessageToDeleteAfterEnd(int msgId) {
        getUserCaches().getDataCache(RocketDataCache.class)
                .addMessageToDelete(msgId);
    }

    public void addMessageToDeleteAfterEnd(int msgId, UserCaches userCaches) {
        userCaches.getDataCache(RocketDataCache.class)
                .addMessageToDelete(msgId);
    }

    public void dropUserMenu() {
        getUserCaches().getDataCache(RocketDataCache.class).getMenuHistoryContainer().deleteUntil(RocketMenu.class, RocketMenu.MAIN_MENU);
    }
}
