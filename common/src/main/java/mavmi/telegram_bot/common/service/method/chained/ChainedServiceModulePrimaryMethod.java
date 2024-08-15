package mavmi.telegram_bot.common.service.method.chained;

import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

import java.util.List;

@FunctionalInterface
public interface ChainedServiceModulePrimaryMethod<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> prepareMethodsChain(Rq rq);
}
