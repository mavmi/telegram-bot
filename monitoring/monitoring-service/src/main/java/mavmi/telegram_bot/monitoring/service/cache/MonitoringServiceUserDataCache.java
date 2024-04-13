package mavmi.telegram_bot.monitoring.service.cache;

import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.service.menu.Menu;

public class MonitoringServiceUserDataCache extends UserDataCache {

    public MonitoringServiceUserDataCache(
            Long userId,
            Menu menu
    ) {
        super(userId, menu);
    }
}
