package mavmi.telegram_bot.common.service.service.direct;

import mavmi.telegram_bot.common.service.service.Service;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

/**
 * Contains business logic of telegram bot's algorithm
 */
public interface DirectService<Rs extends ServiceResponse, Rq extends ServiceRequest> extends Service {

    Rs handleRequest(Rq serviceRequest);
}
