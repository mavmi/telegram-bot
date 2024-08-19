package mavmi.telegram_bot.hb.service.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@RequiredArgsConstructor
public enum HbServiceMenu implements Menu {
    MAIN_MENU(null),
    AUTH(null),
    NOTE_USERNAME(null),
    NOTE_EVENT_GROUPS(NOTE_USERNAME),
    NOTE_EVENT(NOTE_EVENT_GROUPS),
    NOTE_GRADE(NOTE_EVENT),
    SCORE(null),
    FORTUNE_USERNAME(null),
    FORTUNE_PRISE_ID(null),
    PRISE_USERNAME(null),
    PRISE_ID(PRISE_USERNAME);

    private final HbServiceMenu previous;
}
