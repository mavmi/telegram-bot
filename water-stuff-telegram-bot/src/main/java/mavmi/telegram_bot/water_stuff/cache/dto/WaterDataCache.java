package mavmi.telegram_bot.water_stuff.cache.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;

/**
 * {@inheritDoc}
 */
@Getter
@Setter
public class WaterDataCache extends DataCache {

    private String selectedGroup;

    public WaterDataCache(Long userId,
                          WaterStuffServiceMenu menu) {
        super(userId, menu);
    }
}
