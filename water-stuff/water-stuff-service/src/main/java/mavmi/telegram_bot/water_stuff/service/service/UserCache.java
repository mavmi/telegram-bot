package mavmi.telegram_bot.water_stuff.service.service;

import mavmi.telegram_bot.common.utils.cache.AbsUserCache;
import mavmi.telegram_bot.common.utils.service.IMenu;

public class UserCache extends AbsUserCache {
    public UserCache(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName,
            Boolean isPrivilegeGranted
    ) {
        super(userId, menu, username, firstName, lastName, isPrivilegeGranted);
    }
}
