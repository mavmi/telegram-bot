package mavmi.telegram_bot.rocketchat.service;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.rocketchat.cache.RocketAuthCache;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import org.springframework.stereotype.Component;

@Component
public class RocketBotCacheInitializer implements CacheInitializer {

    @Override
    public DataCache initDataCache(long chatId) {
        return new RocketDataCache(chatId);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new RocketAuthCache(true);
    }
}
