package mavmi.telegram_bot.monitoring.cache;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.Privileges;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.UserPrivileges;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Getter
public class MonitoringDataCache extends DataCache {

    private final UserPrivileges userPrivileges;
    private final Privileges privileges = new Privileges();

    public MonitoringDataCache(
            Long userId,
            Menu menu,
            List<PRIVILEGE> privileges
    ) {
        super(userId, menu);
        this.userPrivileges = new UserPrivileges(privileges);
    }
}
