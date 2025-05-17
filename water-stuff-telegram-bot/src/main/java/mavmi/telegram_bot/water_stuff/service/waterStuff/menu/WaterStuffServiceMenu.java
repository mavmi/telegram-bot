package mavmi.telegram_bot.water_stuff.service.waterStuff.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum WaterStuffServiceMenu implements Menu {
    MAIN_MENU(true, "MAIN_MENU"),
    MANAGE_GROUP(true, "MANAGE_GROUP"),
    EDIT(true, "EDIT"),
    EDIT_NAME(false, "EDIT_NAME"),
    EDIT_DIFF(false, "EDIT_DIFF"),
    EDIT_WATER(false, "EDIT_WATER"),
    EDIT_FERTILIZE(false, "EDIT_FERTILIZE"),
    PAUSE(false, "PAUSE"),
    CALENDAR(false, "CALENDAR"),
    ADD(false, "ADD"),
    RM(false, "RM"),
    SELECT_GROUP(false, "SELECT_GROUP"),
    APPROVE(false, "APPROVE");

    private final boolean isPremier;
    private final String name;
}
