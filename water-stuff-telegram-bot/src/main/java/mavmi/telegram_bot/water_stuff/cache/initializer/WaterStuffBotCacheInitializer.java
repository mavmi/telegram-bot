package mavmi.telegram_bot.water_stuff.cache.initializer;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterAuthCache;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.service.database.WaterStuffDatabaseService;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaterStuffBotCacheInitializer implements CacheInitializer {

    private final UserAuthentication userAuthentication;
    private final WaterStuffDatabaseService databaseService;

    @Override
    public DataCache initDataCache(long chatId) {
        WaterDataCache dataCache = new WaterDataCache(chatId, WaterStuffServiceMenu.MAIN_MENU);
        databaseService.findByUserId(chatId).forEach(dataCache::addGroup);
        return dataCache;
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new WaterAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.WATER_STUFF_BOT));
    }
}
