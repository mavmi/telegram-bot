package mavmi.telegram_bot.monitoring.service.cache;

import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.service.menu.IMenu;

public class UserDataCache extends AbstractUserDataCache {

    public UserDataCache(
            Long userId,
            IMenu menu
    ) {
        super(userId, menu);
    }
}
