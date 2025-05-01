package mavmi.telegram_bot.lib.menu_engine_starter.service.serviceComponents.method;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

/**
 * Functional interface for all significant service methods
 */
@FunctionalInterface
public interface ServiceMethod<RequestType extends ServiceRequest> {

    void process(RequestType request);
}
