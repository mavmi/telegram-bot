package mavmi.telegram_bot.monitoring.service.monitoring.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum MonitoringServiceMenu implements Menu {
    MAIN_MENU(null),
    HOST(MAIN_MENU),
    APPS(MAIN_MENU),
    PRIVILEGES_INIT(MAIN_MENU),
    PRIVILEGES(MAIN_MENU),
    PRIVILEGES_ADD(PRIVILEGES),
    PRIVILEGES_DELETE(PRIVILEGES),
    PMS(MAIN_MENU),
    PMS_EDIT(PMS),
    BOT_ACCESS_INIT(MAIN_MENU),
    BOT_ACCESS(MAIN_MENU);

    private final MonitoringServiceMenu parent;
}
