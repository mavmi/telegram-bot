package mavmi.telegram_bot.rocketchat.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum RocketMenu implements Menu {
    MAIN_MENU(null),
    AUTH(MAIN_MENU),
    AUTH_ENTER_LOGIN(MAIN_MENU),
    AUTH_ENTER_PASSWORD(MAIN_MENU);

    private final RocketMenu parent;
}
