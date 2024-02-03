package mavmi.telegram_bot.common.service;

import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;

public abstract class AbstractService {

    public abstract AbstractUserDataCache initCache();
}
