package mavmi.telegram_bot.water_stuff.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.AuthCache;

/**
 * {@inheritDoc}
 */
@Getter
@Setter
public class WaterAuthCache extends AuthCache {

    public WaterAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
