package mavmi.telegram_bot.hb.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.service.menu.Menu;

@Getter
@Setter
public class HbDataCache extends DataCache {

    private String hbSelectedUsername;
    private String hbSelectedEventGroup;
    private String hbSelectedEvent;
    private double hbSelectedGrade;
    private String hbSelectedPriseId;

    public HbDataCache(Long userId, Menu menu) {
        super(userId, menu);
    }
}
