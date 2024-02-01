package mavmi.telegram_bot.water_stuff.service.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.service.menu.IMenu;

@Getter
@Setter
public class UserDataCache extends AbstractUserDataCache {

    private String selectedGroup;
    private IMenu premierMenu;

    public UserDataCache(
            Long userId,
            IMenu premierMenu,
            IMenu menu
    ) {
        super(userId, menu);
        this.premierMenu = premierMenu;
    }
}
