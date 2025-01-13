package mavmi.telegram_bot.shakal.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum ShakalServiceMenu implements Menu {
    MAIN_MENU(null),
    APOLOCHEESE(MAIN_MENU),
    DICE(MAIN_MENU),
    HOROSCOPE(MAIN_MENU);

    private final ShakalServiceMenu parent;
}
