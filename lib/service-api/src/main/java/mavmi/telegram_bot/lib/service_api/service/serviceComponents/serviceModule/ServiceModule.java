package mavmi.telegram_bot.lib.service_api.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

/**
 * Module (a logical part) of service
 */
public interface ServiceModule<RequestType extends ServiceRequest> {

    void handleRequest(RequestType request);
}
