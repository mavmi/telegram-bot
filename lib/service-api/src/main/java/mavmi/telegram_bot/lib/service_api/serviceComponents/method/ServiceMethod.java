package mavmi.telegram_bot.lib.service_api.serviceComponents.method;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

/**
 * Functional interface for all significant service methods
 */
@FunctionalInterface
public interface ServiceMethod<RequestType extends ServiceRequest> {

    void process(RequestType request);
}
