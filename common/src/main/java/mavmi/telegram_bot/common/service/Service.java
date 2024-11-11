package mavmi.telegram_bot.common.service;

import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;

/**
 * Contains business logic of telegram bot's algorithm
 */
public interface Service<Rq extends ServiceRequest> {

    void handleRequest(Rq serviceRequest);
    DataCache initDataCache(long chatId);
    AuthCache initAuthCache(long chatId);
}
