package mavmi.telegram_bot.shakal.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum ShakalServiceMenu implements Menu {
    MAIN_MENU("MAIN_MENU"),
    APOLOCHEESE("APOLOCHEESE"),
    DICE("DICE"),
    HOROSCOPE("HOROSCOPE");

    private final String name;
}
