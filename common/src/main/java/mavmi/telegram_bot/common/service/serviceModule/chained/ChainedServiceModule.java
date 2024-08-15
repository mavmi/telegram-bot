package mavmi.telegram_bot.common.service.serviceModule.chained;

import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

import java.util.List;

public interface ChainedServiceModule<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> prepareMethodsChain(Rq rq);
}
