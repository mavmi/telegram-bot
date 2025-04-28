package mavmi.telegram_bot.water_stuff.service.waterStuff;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.water_stuff.cache.WaterAuthCache;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaterStuffBotCacheInitializer implements CacheInitializer {

    private final UserAuthentication userAuthentication;

    @Override
    public DataCache initDataCache(long chatId) {
        return new WaterDataCache(chatId, WaterStuffServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new WaterAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.WATER_STUFF_BOT));
    }
}
