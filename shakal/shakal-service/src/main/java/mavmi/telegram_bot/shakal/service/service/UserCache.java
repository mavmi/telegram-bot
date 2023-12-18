package mavmi.telegram_bot.shakal.service.service;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.cache.AbsUserCache;

@Getter
@Setter
public class UserCache extends AbsUserCache {
    private int userDice;
    private int botDice;

    public UserCache(
            Long userId,
            Menu menu,
            String username,
            String firstName,
            String lastName
    ) {
        super(userId, menu, username, firstName, lastName);
    }
}
