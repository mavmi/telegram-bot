package mavmi.telegram_bot.common.service.container.impl;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.ServiceRequest;
import mavmi.telegram_bot.common.service.service.ServiceResponse;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Contains info which service module to use to handle incoming message
 */
@RequiredArgsConstructor
public class MenuToServiceModuleContainer<ResponseType extends ServiceResponse, RequestType extends ServiceRequest> {

    private final Map<Menu, ServiceModule<ResponseType, RequestType>> menuToServiceElement;

    @Nullable
    public ServiceModule<ResponseType, RequestType> get(Menu menu) {
        return menuToServiceElement.get(menu);
    }

    public void put(Menu key, ServiceModule<ResponseType, RequestType> value) {
        menuToServiceElement.put(key, value);
    }
}
