package mavmi.telegram_bot.lib.service_api;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

/**
 * Contains business logic of telegram bot's algorithm
 */
public interface Service<Rq extends ServiceRequest> {

    void handleRequest(Rq serviceRequest);
}
