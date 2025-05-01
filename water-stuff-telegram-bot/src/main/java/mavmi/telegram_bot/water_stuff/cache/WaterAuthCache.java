package mavmi.telegram_bot.water_stuff.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;

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
