package mavmi.telegram_bot.common.service.method.direct;

import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

/**
 * Functional interface for all significant service methods
 */
@FunctionalInterface
public interface ServiceMethod<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    Rs process(Rq request);
}
