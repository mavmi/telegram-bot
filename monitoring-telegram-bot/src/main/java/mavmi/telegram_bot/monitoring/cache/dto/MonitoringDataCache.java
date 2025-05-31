package mavmi.telegram_bot.monitoring.cache.dto;

import lombok.Getter;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.monitoring.cache.dto.inner.PmsCache;

/**
 * {@inheritDoc}
 */
@Getter
public class MonitoringDataCache extends DataCache {

    private final PmsCache pmsCache = new PmsCache();

    public MonitoringDataCache(Long userId, Menu menu) {
        super(userId, menu);
    }
}
