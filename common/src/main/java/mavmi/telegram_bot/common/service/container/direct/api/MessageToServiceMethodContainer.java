package mavmi.telegram_bot.common.service.container.direct.api;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Contains info which service method to use to handle incoming message
 */
@RequiredArgsConstructor
public abstract class MessageToServiceMethodContainer<ReturnType extends ServiceResponse, RequestType extends ServiceRequest> {

    @Nullable
    private final Map<String, ServiceMethod<ReturnType, RequestType>> requestToMethod;
    private final ServiceMethod<ReturnType, RequestType> defaultMethod;

    public MessageToServiceMethodContainer(Map<String, ServiceMethod<ReturnType, RequestType>> requestToMethod) {
        this(requestToMethod, null);
    }

    public MessageToServiceMethodContainer(ServiceMethod<ReturnType, RequestType> defaultMethod) {
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
