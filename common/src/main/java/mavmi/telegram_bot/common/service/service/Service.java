package mavmi.telegram_bot.common.service.service;

import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;

public interface Service {

    DataCache initDataCache(long chatId);
    AuthCache initAuthCache(long chatId);
}
