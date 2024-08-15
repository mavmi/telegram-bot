package mavmi.telegram_bot.common.service.serviceModule.direct;

import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

/**
 * Module (a logical part) of service
 */
public interface ServiceModule<ResponseType extends ServiceResponse, RequestType extends ServiceRequest> {

    ResponseType handleRequest(RequestType request);
}
