package mavmi.telegram_bot.rocketchat.cache;

import mavmi.telegram_bot.common.cache.api.AuthCache;

public class RocketchatServiceAuthCache extends AuthCache {

    public RocketchatServiceAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
