package mavmi.telegram_bot.common.service.container.impl;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.dto.api.Rs;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import org.springframework.lang.Nullable;

import java.util.Map;

@RequiredArgsConstructor
public class MenuToServiceServiceModuleContainer<ResponseType extends Rs, RequestType extends Rq> {

    private final Map<Menu, ServiceModule<ResponseType, RequestType>> menuToServiceElement;

    @Nullable
    public ServiceModule<ResponseType, RequestType> get(Menu menu) {
        return menuToServiceElement.get(menu);
    }

    public void put(Menu key, ServiceModule<ResponseType, RequestType> value) {
        menuToServiceElement.put(key, value);
    }
}
