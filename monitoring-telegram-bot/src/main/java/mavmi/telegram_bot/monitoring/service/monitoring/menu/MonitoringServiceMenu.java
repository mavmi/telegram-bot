package mavmi.telegram_bot.monitoring.service.monitoring.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum MonitoringServiceMenu implements Menu {
    MAIN_MENU("MAIN_MENU"),
    HOST("HOST"),
    APPS("APPS");

    private final String name;
}
