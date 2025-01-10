package mavmi.telegram_bot.monitoring.cache;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.Privileges;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Getter
public class MonitoringDataCache extends DataCache {

    private final Privileges privileges;

    public MonitoringDataCache(
            Long userId,
            Menu menu,
            List<PRIVILEGE> privileges
    ) {
        super(userId, menu);
        this.privileges = new Privileges(privileges);
    }
}
