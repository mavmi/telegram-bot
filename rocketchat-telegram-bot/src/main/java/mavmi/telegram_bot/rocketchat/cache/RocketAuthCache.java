package mavmi.telegram_bot.rocketchat.cache;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;

public class RocketAuthCache extends AuthCache {

    public RocketAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
