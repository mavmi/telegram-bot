package mavmi.telegram_bot.monitoring.service.monitoring.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum MonitoringServiceMenu implements Menu {
    MAIN_MENU(null, "MAIN_MENU"),
    HOST(MAIN_MENU, "HOST"),
    APPS(MAIN_MENU, "APPS");

    private final MonitoringServiceMenu parent;
    private final String name;

    @Override
    public MonitoringServiceMenu findByName(String name) {
        return MonitoringServiceMenu.valueOf(name);
    }
}
