package mavmi.telegram_bot.common.service.serviceComponents.method;

import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;

/**
 * Functional interface for all significant service methods
 */
@FunctionalInterface
public interface ServiceMethod<RequestType extends ServiceRequest> {

    void process(RequestType request);
}
