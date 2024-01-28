package mavmi.telegram_bot.water_stuff.service.service;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.cache.AbsUserCache;
import mavmi.telegram_bot.common.service.IMenu;

@Getter
@Setter
public class UserCache extends AbsUserCache {

    private String selectedGroup;
    private IMenu premierMenu;

    public UserCache(
            Long userId,
            IMenu premierMenu,
            IMenu menu,
            String username,
            String firstName,
            String lastName
    ) {
        super(userId, menu, username, firstName, lastName);
        this.premierMenu = premierMenu;
    }
}
