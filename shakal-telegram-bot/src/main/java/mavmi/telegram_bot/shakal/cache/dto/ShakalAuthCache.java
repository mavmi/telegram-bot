package mavmi.telegram_bot.shakal.cache.dto;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;

/**
 * {@inheritDoc}
 */
public class ShakalAuthCache extends AuthCache {

    public ShakalAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
