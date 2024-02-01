package mavmi.telegram_bot.common.service;

import mavmi.telegram_bot.common.cache.Cache;
import mavmi.telegram_bot.common.cache.AbstractUserCache;

public abstract class AbstractService<U extends AbstractUserCache> {

    protected Cache<U> cache;

    public AbstractService(Cache<U> cache) {
        this.cache = cache;
    }
}
