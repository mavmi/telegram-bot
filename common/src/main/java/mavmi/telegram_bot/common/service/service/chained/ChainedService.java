package mavmi.telegram_bot.common.service.service.chained;

import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.service.Service;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

import java.util.List;

public interface ChainedService<Rs extends ServiceResponse, Rq extends ServiceRequest> extends Service {

    List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> prepareMethodsChain(Rq request);
    Rs handleRequest(Rq serviceRequest, ChainedServiceModuleSecondaryMethod<Rs, Rq> method);
}
