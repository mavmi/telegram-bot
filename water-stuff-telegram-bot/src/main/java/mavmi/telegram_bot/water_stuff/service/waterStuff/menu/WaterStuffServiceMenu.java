package mavmi.telegram_bot.water_stuff.service.waterStuff.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum WaterStuffServiceMenu implements Menu {
    MAIN_MENU(null, true),
    MANAGE_GROUP(MAIN_MENU, true),
    EDIT(MAIN_MENU, true),
    EDIT_NAME(EDIT, false),
    EDIT_DIFF(EDIT, false),
    EDIT_WATER(EDIT, false),
    EDIT_FERTILIZE(EDIT, false),
    PAUSE(EDIT, false),
    CALENDAR(MANAGE_GROUP, false),
    ADD(MAIN_MENU, false),
    RM(MAIN_MENU, false),
    SELECT_GROUP(MAIN_MENU, false),
    APPROVE(null, false);

    private final WaterStuffServiceMenu parent;
    private final boolean isPremier;
}
