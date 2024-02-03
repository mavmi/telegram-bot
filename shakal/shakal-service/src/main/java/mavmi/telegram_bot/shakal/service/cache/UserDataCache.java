package mavmi.telegram_bot.shakal.service.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.shakal.service.service.menu.Menu;

@Getter
@Setter
public class UserDataCache extends AbstractUserDataCache {
    private int userDice;
    private int botDice;

    public UserDataCache(
            Long userId,
            Menu menu
    ) {
        super(userId, menu);
    }
}
