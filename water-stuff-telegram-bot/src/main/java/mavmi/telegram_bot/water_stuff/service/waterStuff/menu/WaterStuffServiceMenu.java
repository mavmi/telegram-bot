package mavmi.telegram_bot.water_stuff.service.waterStuff.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum WaterStuffServiceMenu implements Menu {
    MAIN_MENU(null, true, "MAIN_MENU"),
    MANAGE_GROUP(MAIN_MENU, true, "MANAGE_GROUP"),
    EDIT(MAIN_MENU, true, "EDIT"),
    EDIT_NAME(EDIT, false, "EDIT_NAME"),
    EDIT_DIFF(EDIT, false, "EDIT_DIFF"),
    EDIT_WATER(EDIT, false, "EDIT_WATER"),
    EDIT_FERTILIZE(EDIT, false, "EDIT_FERTILIZE"),
    PAUSE(EDIT, false, "PAUSE"),
    CALENDAR(MANAGE_GROUP, false, "CALENDAR"),
    ADD(MAIN_MENU, false, "ADD"),
    RM(MAIN_MENU, false, "RM"),
    SELECT_GROUP(MAIN_MENU, false, "SELECT_GROUP"),
    APPROVE(null, false, "APPROVE");

    private final WaterStuffServiceMenu parent;
    private final boolean isPremier;
    private final String name;

    @Override
    public WaterStuffServiceMenu findByName(String name) {
        return WaterStuffServiceMenu.valueOf(name);
    }
}
