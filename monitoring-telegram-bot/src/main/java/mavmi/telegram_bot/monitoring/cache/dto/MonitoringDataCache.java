package mavmi.telegram_bot.monitoring.cache.dto;

import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.monitoring.cache.dto.inner.PmsCache;

/**
 * {@inheritDoc}
 */
public class MonitoringDataCache extends DataCache {

    private final PmsCache pmsCache = new PmsCache();

    public MonitoringDataCache(Long userId, Menu menu) {
        super(userId, menu);
    }

    public void setSelectedParameter(Parameter parameter) {
        pmsCache.setSelectedParameter(parameter);
    }

    public Parameter getSelectedParameter() {
        return pmsCache.getSelectedParameter();
    }
}
