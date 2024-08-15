package mavmi.telegram_bot.common.service.method.chained;

import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

@FunctionalInterface
public interface ChainedServiceModuleSecondaryMethod<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    Rs process(Rq rq);
}
