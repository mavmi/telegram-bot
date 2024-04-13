package mavmi.telegram_bot.common.service.method;

import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.dto.api.Rs;

@FunctionalInterface
public interface ServiceMethod<ResponseType extends Rs, RequestType extends Rq> {
    ResponseType process(RequestType request);
}
