package mavmi.telegram_bot.monitoring.service.monitoring;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.monitoring.cache.MonitoringAuthCache;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringBotCacheInitializer implements CacheInitializer {

    private final UserAuthentication userAuthentication;

    @Override
    public DataCache initDataCache(long chatId) {
        return new MonitoringDataCache(chatId, MonitoringServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new MonitoringAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.MONITORING_BOT));
    }
}
