package mavmi.telegram_bot.monitoring.cache;

import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.service.menu.Menu;

public class MonitoringServiceDataCache extends DataCache {

    public MonitoringServiceDataCache(
            Long userId,
            Menu menu
    ) {
        super(userId, menu);
    }
}
