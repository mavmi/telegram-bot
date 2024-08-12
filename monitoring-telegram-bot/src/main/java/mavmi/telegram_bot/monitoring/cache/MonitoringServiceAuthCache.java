package mavmi.telegram_bot.monitoring.cache;

import mavmi.telegram_bot.common.cache.api.AuthCache;

public class MonitoringServiceAuthCache extends AuthCache {

    public MonitoringServiceAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
