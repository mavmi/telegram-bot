package mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;

public interface CacheInitializer {
    DataCache initDataCache(long chatId);
    AuthCache initAuthCache(long chatId);
}
