package mavmi.telegram_bot.monitoring.cache;

import lombok.Getter;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.BotAccessManagement;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.PmsManagement;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.PrivilegesManagement;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.UserPrivileges;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Getter
public class MonitoringDataCache extends DataCache {

    private final UserPrivileges userPrivileges;
    private final PrivilegesManagement privilegesManagement = new PrivilegesManagement();
    private final PmsManagement pmsManagement = new PmsManagement();
    private final BotAccessManagement botAccessManagement = new BotAccessManagement();

    public MonitoringDataCache(Long userId,
            Menu menu,
            List<PRIVILEGE> privileges) {
        super(userId, menu);
        this.userPrivileges = new UserPrivileges(privileges);
    }
}
