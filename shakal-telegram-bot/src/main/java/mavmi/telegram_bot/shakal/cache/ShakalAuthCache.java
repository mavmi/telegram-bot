package mavmi.telegram_bot.shakal.cache;

import mavmi.telegram_bot.common.cache.api.AuthCache;

/**
 * {@inheritDoc}
 */
public class ShakalAuthCache extends AuthCache {

    public ShakalAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
