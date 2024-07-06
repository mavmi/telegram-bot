package mavmi.telegram_bot.common.service.serviceModule;

import mavmi.telegram_bot.common.service.service.ServiceRequest;
import mavmi.telegram_bot.common.service.service.ServiceResponse;

/**
 * Module (a logical part) of service
 */
public interface ServiceModule<ResponseType extends ServiceResponse, RequestType extends ServiceRequest> {

    ResponseType handleRequest(RequestType request);
}
