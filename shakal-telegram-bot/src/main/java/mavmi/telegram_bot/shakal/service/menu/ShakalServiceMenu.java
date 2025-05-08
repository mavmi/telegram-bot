package mavmi.telegram_bot.shakal.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum ShakalServiceMenu implements Menu {
    MAIN_MENU(null, "MAIN_MENU"),
    APOLOCHEESE(MAIN_MENU, "APOLOCHEESE"),
    DICE(MAIN_MENU, "DICE"),
    HOROSCOPE(MAIN_MENU, "HOROSCOPE");

    private final ShakalServiceMenu parent;
    private final String name;

    @Override
    public ShakalServiceMenu findByName(String name) {
        return ShakalServiceMenu.valueOf(name);
    }
}
