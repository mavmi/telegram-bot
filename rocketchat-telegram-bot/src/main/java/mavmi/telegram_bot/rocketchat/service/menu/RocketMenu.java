package mavmi.telegram_bot.rocketchat.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum RocketMenu implements Menu {
    MAIN_MENU("MAIN_MENU"),
    AUTH("AUTH"),
    AUTH_ENTER_LOGIN("AUTH_ENTER_LOGIN"),
    AUTH_ENTER_PASSWORD("AUTH_ENTER_PASSWORD");

    private final String name;
}
