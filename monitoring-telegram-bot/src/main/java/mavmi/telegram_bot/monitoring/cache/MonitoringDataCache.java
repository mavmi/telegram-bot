package mavmi.telegram_bot.monitoring.cache;

import mavmi.telegram_bot.common.service.menu.Menu;

/**
 * {@inheritDoc}
 */
public class MonitoringDataCache extends mavmi.telegram_bot.common.cache.api.DataCache {

    public MonitoringDataCache(
            Long userId,
            Menu menu
    ) {
        super(userId, menu);
    }
}
