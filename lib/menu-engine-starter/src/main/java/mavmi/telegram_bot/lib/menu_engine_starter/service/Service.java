package mavmi.telegram_bot.lib.menu_engine_starter.service;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

/**
 * Contains business logic of telegram bot's algorithm
 */
public interface Service<Rq extends ServiceRequest> {

    void handleRequest(Rq serviceRequest);
}
