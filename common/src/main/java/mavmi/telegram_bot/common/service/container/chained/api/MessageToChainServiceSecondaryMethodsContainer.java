package mavmi.telegram_bot.common.service.container.chained.api;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class MessageToChainServiceSecondaryMethodsContainer<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    @Nullable
    private final Map<String, List<ChainedServiceModuleSecondaryMethod<Rs, Rq>>> requestToMethods;
    private final List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> defaultMethods;

    public MessageToChainServiceSecondaryMethodsContainer(Map<String, List<ChainedServiceModuleSecondaryMethod<Rs, Rq>>> requestToMethods) {
        this(requestToMethods, null);
    }

    public MessageToChainServiceSecondaryMethodsContainer(List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> defaultMethods) {
        this(null, defaultMethods);
    }

    public List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> getMethods(@Nullable String request) {
        if (request == null) {
            return defaultMethods;
        }

        if (requestToMethods != null) {
            List<ChainedServiceModuleSecondaryMethod<Rs, Rq>> methods = requestToMethods.get(request);
            if (methods != null) {
                return methods;
            }
        }

        return defaultMethods;
    }
}
