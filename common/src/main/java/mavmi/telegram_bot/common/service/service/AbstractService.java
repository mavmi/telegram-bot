package mavmi.telegram_bot.common.service.service;

import mavmi.telegram_bot.common.cache.userData.UserDataCache;

public abstract class AbstractService {

    public abstract UserDataCache initCache();
}
