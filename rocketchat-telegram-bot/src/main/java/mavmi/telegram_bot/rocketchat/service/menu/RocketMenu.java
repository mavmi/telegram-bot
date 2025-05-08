package mavmi.telegram_bot.rocketchat.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum RocketMenu implements Menu {
    MAIN_MENU(null, "MAIN_MENU"),
    AUTH(MAIN_MENU, "AUTH"),
    AUTH_ENTER_LOGIN(MAIN_MENU, "AUTH_ENTER_LOGIN"),
    AUTH_ENTER_PASSWORD(MAIN_MENU, "AUTH_ENTER_PASSWORD");

    private final RocketMenu parent;
    private final String name;

    @Override
    public RocketMenu findByName(String name) {
        return RocketMenu.valueOf(name);
    }
}
