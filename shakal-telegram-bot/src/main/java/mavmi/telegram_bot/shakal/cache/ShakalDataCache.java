package mavmi.telegram_bot.shakal.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;

/**
 * {@inheritDoc}
 */
@Getter
@Setter
public class ShakalDataCache extends DataCache {

    private int userDice;
    private int botDice;

    public ShakalDataCache(
            Long userId,
            ShakalServiceMenu menu
    ) {
        super(userId, menu);
    }
}
