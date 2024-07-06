package mavmi.telegram_bot.water_stuff.service.water_stuff.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum WaterStuffServiceMenu implements Menu {
    MAIN_MENU(true),
    MANAGE_GROUP(true),
    EDIT(true),
    EDIT_NAME(false),
    EDIT_DIFF(false),
    EDIT_WATER(false),
    EDIT_FERTILIZE(false),
    PAUSE(false),
    CALENDAR(false),
    ADD(false),
    RM(false),
    SELECT_GROUP(false),
    APPROVE(false);

    private final boolean isPremier;
}
