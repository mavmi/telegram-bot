package mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum WaterStuffServiceMenu implements Menu {
    MAIN_MENU(true),
    MANAGE_GROUP(true),
    ADD(false),
    EDIT(false),
    RM(false),
    SELECT_GROUP(false),
    APPROVE(false);

    private final boolean isPremier;
}
