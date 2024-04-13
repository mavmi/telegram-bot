package mavmi.telegram_bot.shakal.service.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.ShakalServiceMenu;

@Getter
@Setter
public class ShakalServiceUserDataCache extends UserDataCache {
    private int userDice;
    private int botDice;

    public ShakalServiceUserDataCache(
            Long userId,
            ShakalServiceMenu shakalServiceMenu
    ) {
        super(userId, shakalServiceMenu);
    }
}
