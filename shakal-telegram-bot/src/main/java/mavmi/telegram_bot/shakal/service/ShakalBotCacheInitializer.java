package mavmi.telegram_bot.shakal.service;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.shakal.cache.ShakalAuthCache;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import org.springframework.stereotype.Component;

@Component
public class ShakalBotCacheInitializer implements CacheInitializer {

    @Override
    public DataCache initDataCache(long chatId) {
        return new ShakalDataCache(chatId, ShakalServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new ShakalAuthCache(true);
    }
}
