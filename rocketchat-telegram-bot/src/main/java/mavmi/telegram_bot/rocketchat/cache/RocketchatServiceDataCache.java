package mavmi.telegram_bot.rocketchat.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;

@Getter
@Setter
public class RocketchatServiceDataCache extends DataCache {

    private String rocketchatUsername;
    private String rocketchatPasswordHash;
    private String rocketchatToken;
    private Long rocketchatTokenExpiryDate;

    public RocketchatServiceDataCache(Long userId) {
        super(userId, RocketchatServiceMenu.MAIN_MENU);
    }
}
