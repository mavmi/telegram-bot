package mavmi.telegram_bot.hb.cache;

import mavmi.telegram_bot.common.cache.api.AuthCache;

public class HbAuthCache extends AuthCache {

    public HbAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
