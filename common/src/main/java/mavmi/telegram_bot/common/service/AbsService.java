package mavmi.telegram_bot.common.service;

import mavmi.telegram_bot.common.service.cache.ServiceCache;
import mavmi.telegram_bot.common.service.cache.AbsUserCache;

public abstract class AbsService<U extends AbsUserCache> {

    protected ServiceCache<U> serviceCache;

    public AbsService(ServiceCache<U> serviceCache) {
        this.serviceCache = serviceCache;
    }
}
