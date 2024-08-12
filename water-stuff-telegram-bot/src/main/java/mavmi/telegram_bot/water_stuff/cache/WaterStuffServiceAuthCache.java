package mavmi.telegram_bot.water_stuff.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.AuthCache;

@Getter
@Setter
public class WaterStuffServiceAuthCache extends AuthCache {

    public WaterStuffServiceAuthCache(boolean accessGranted) {
        super(accessGranted);
    }
}
