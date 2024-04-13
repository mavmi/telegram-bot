package mavmi.telegram_bot.common.service.serviceModule;

import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.dto.api.Rs;

public interface ServiceModule<ResponseType extends Rs, RequestType extends Rq> {
    ResponseType process(RequestType request);
}
