package mavmi.telegram_bot.monitoring.cache;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;

/**
 * {@inheritDoc}
 */
public class MonitoringAuthCache extends AuthCache {

    public MonitoringAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
