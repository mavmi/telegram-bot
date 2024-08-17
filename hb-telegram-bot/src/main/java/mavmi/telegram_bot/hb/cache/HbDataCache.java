package mavmi.telegram_bot.hb.cache;

import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.service.menu.Menu;

public class HbDataCache extends DataCache {

    public HbDataCache(Long userId, Menu menu) {
        super(userId, menu);
    }
}
