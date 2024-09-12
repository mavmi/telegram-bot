package mavmi.telegram_bot.rocketchat.cache;

import mavmi.telegram_bot.common.cache.api.AuthCache;

public class RocketAuthCache extends AuthCache {

    public RocketAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
