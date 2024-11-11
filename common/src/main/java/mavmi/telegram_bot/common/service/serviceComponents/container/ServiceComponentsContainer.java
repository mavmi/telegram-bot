package mavmi.telegram_bot.common.service.serviceComponents.container;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores service's methods and submodules
 */
public class ServiceComponentsContainer<RequestType extends ServiceRequest> {

    private final Map<String, ServiceMethod<RequestType>> msgToMethod = new HashMap<>();
    private final Map<String, List<ServiceMethod<RequestType>>> msgToMethods = new HashMap<>();
    private final Map<Menu, ServiceModule<RequestType>> menuToModule = new HashMap<>();

    @Getter
    @Setter
    @Accessors(chain = true)
    private ServiceMethod<RequestType> defaultServiceMethod;

    @Getter
    @Setter
    @Accessors(chain = true)
    private List<ServiceMethod<RequestType>> defaultServiceMethods;

    public ServiceComponentsContainer<RequestType> add(String msg, ServiceMethod<RequestType> serviceMethod) {
        msgToMethod.put(msg, serviceMethod);
        return this;
    }

    public ServiceComponentsContainer<RequestType> add(String msg, ServiceMethod<RequestType>... serviceMethods) {
        return add(msg, Arrays.asList(serviceMethods));
    }

    public ServiceComponentsContainer<RequestType> add(String msg, List<ServiceMethod<RequestType>> serviceMethods) {
        msgToMethods.put(msg, serviceMethods);
        return this;
    }

    public ServiceComponentsContainer<RequestType> add(Menu menu, ServiceModule<RequestType> serviceModule) {
        menuToModule.put(menu, serviceModule);
        return this;
    }

    @Nullable
    public ServiceMethod<RequestType> getMethod(String msg) {
        ServiceMethod<RequestType> method = msgToMethod.get(msg);
        return (method == null) ? defaultServiceMethod : method;
    }

    @Nullable
    public List<ServiceMethod<RequestType>> getMethods(String msg) {
        List<ServiceMethod<RequestType>> methods = msgToMethods.get(msg);
        return (methods == null) ? defaultServiceMethods : methods;
    }

    @Nullable
    public ServiceModule<RequestType> getModule(Menu menu) {
        return menuToModule.get(menu);
    }
}
