package mavmi.telegram_bot.water_stuff.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;

/**
 * {@inheritDoc}
 */
@Getter
@Setter
public class WaterDataCache extends DataCache {

    private String selectedGroup;

    public WaterDataCache(
            Long userId,
            WaterStuffServiceMenu menu
    ) {
        super(userId, menu);
    }
}
