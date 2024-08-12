package mavmi.telegram_bot.common.service.method;

import mavmi.telegram_bot.common.service.service.ServiceRequest;
import mavmi.telegram_bot.common.service.service.ServiceResponse;

/**
 * Functional interface for all significant service methods
 */
@FunctionalInterface
public interface ServiceMethod<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    Rs process(Rq request);
}
