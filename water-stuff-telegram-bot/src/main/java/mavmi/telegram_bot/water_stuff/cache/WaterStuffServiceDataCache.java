package mavmi.telegram_bot.water_stuff.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;

@Getter
@Setter
public class WaterStuffServiceDataCache extends DataCache {

    private String selectedGroup;

    public WaterStuffServiceDataCache(
            Long userId,
            WaterStuffServiceMenu menu
    ) {
        super(userId, menu);
    }
}
