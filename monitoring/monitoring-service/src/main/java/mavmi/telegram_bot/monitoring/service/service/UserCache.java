package mavmi.telegram_bot.monitoring.service.service;

import mavmi.telegram_bot.common.service.cache.AbsUserCache;

public class UserCache extends AbsUserCache {
    public UserCache(
            Long userId,
            String username,
            String firstName,
            String lastName
    ) {
        super(userId, username, firstName, lastName);
    }
}
