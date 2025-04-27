package mavmi.telegram_bot.lib.service_api;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;

/**
 * Contains business logic of telegram bot's algorithm
 */
public interface Service<Rq extends ServiceRequest> {

    void handleRequest(Rq serviceRequest);
    DataCache initDataCache(long chatId);
    AuthCache initAuthCache(long chatId);
}
