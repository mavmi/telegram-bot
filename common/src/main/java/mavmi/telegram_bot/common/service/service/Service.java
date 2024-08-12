package mavmi.telegram_bot.common.service.service;

import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;

/**
 * Contains business logic of telegram bot's algorithm
 */
public interface Service<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    Rs handleRequest(Rq serviceRequest);
    DataCache initDataCache(long chatId);
    AuthCache initAuthCache(long chatId);
}
