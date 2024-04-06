package mavmi.telegram_bot.common.service.container.api;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.dto.api.Rs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import org.springframework.lang.Nullable;

import java.util.Map;

@RequiredArgsConstructor
public abstract class MessageToHandlerContainer<ReturnType extends Rs, RequestType extends Rq> {

    @Nullable
    private final Map<String, ServiceMethod<ReturnType, RequestType>> requestToMethod;
    private final ServiceMethod<ReturnType, RequestType> defaultMethod;

    public MessageToHandlerContainer(ServiceMethod<ReturnType, RequestType> defaultMethod) {
        this(null, defaultMethod);
    }

    public ServiceMethod<ReturnType, RequestType> getMethod(@Nullable String request) {
        if (request == null) {
            return defaultMethod;
        }

        if (requestToMethod != null) {
            ServiceMethod<ReturnType, RequestType> method = requestToMethod.get(request);
            if (method != null) {
                return method;
            }
        }

        return defaultMethod;
    }
}
