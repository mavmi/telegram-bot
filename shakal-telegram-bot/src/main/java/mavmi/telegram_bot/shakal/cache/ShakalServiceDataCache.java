package mavmi.telegram_bot.shakal.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;

@Getter
@Setter
public class ShakalServiceDataCache extends DataCache {

    private int userDice;
    private int botDice;

    public ShakalServiceDataCache(
            Long userId,
            ShakalServiceMenu menu
    ) {
        super(userId, menu);
    }
}
