package mavmi.telegram_bot.monitoring.service.service;

import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.common.cache.AbstractUserCache;

public class UserCache extends AbstractUserCache {

    public UserCache(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName
    ) {
        super(userId, menu, username, firstName, lastName);
    }
}
