package mavmi.telegram_bot.water_stuff.service.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@Setter
public class WaterStuffServiceUserDataCache extends UserDataCache {

    private String selectedGroup;

    public WaterStuffServiceUserDataCache(
            Long userId,
            Menu menu
    ) {
        super(userId, menu);
    }
}
