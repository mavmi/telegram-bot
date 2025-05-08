package mavmi.telegram_bot.lib.menu_engine_starter.handler.api;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;

@Getter
@RequiredArgsConstructor
public abstract class MenuRequestHandler<RequestType extends ServiceRequest> {

    protected final MenuEngine menuEngine;
    protected final Menu menu;

    @PostConstruct
    public void register() {
        menuEngine.registerHandler(this);
    }

    public abstract void handleRequest(RequestType request);
}
