package mavmi.telegram_bot.common.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;

/**
 * Module (a logical part) of service
 */
public interface ServiceModule<RequestType extends ServiceRequest> {

    void handleRequest(RequestType request);
}
