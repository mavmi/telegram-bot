package mavmi.telegram_bot.common.service.container.chained.api;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModulePrimaryMethod;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;
import org.springframework.lang.Nullable;

import java.util.Map;

@RequiredArgsConstructor
public abstract class MessageToChainServicePrimaryMethodContainer<Rs extends ServiceResponse, Rq extends ServiceRequest> {

    @Nullable
    private final Map<String, ChainedServiceModulePrimaryMethod<Rs, Rq>> requestToMethod;
    private final ChainedServiceModulePrimaryMethod<Rs, Rq> defaultMethod;

    public MessageToChainServicePrimaryMethodContainer(Map<String, ChainedServiceModulePrimaryMethod<Rs, Rq>> requestToMethod) {
        this(requestToMethod, null);
    }

    public MessageToChainServicePrimaryMethodContainer(ChainedServiceModulePrimaryMethod<Rs, Rq> defaultMethod) {
        this(null, defaultMethod);
    }

    public ChainedServiceModulePrimaryMethod<Rs, Rq> getMethod(@Nullable String request) {
        if (request == null) {
            return defaultMethod;
        }

        if (requestToMethod != null) {
            ChainedServiceModulePrimaryMethod<Rs, Rq> method = requestToMethod.get(request);
            if (method != null) {
                return method;
            }
        }

        return defaultMethod;
    }
}
