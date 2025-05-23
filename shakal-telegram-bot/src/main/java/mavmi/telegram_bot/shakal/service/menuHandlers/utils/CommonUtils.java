package mavmi.telegram_bot.shakal.service.menuHandlers.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.repository.RequestRepository;
import mavmi.telegram_bot.lib.database_starter.repository.UserRepository;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.cache.dto.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.telegramBot.client.ShakalTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class CommonUtils {

    private final UserCachesProvider userCachesProvider;
    private final ShakalTelegramBotSender sender;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private ShakalConstants constants;

    @Autowired
    public void setup(ShakalConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public void dropUserCaches() {
        getUserCaches().getDataCache(ShakalDataCache.class)
                .getMenuHistoryContainer()
                .deleteUntil(ShakalServiceMenu.class, ShakalServiceMenu.MAIN_MENU);
    }
}
