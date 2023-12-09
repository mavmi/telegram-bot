package mavmi.telegram_bot.common.utils.service;

import mavmi.telegram_bot.common.utils.cache.AbsUserCache;
import mavmi.telegram_bot.common.utils.cache.ServiceCache;

public abstract class AbsService<U extends AbsUserCache> {

    protected ServiceCache<U> serviceCache;

    public AbsService(ServiceCache<U> serviceCache) {
        this.serviceCache = serviceCache;
    }
}
