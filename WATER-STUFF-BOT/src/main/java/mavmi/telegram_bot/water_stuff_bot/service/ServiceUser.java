package mavmi.telegram_bot.water_stuff_bot.service;

import mavmi.telegram_bot.common.service.AbsServiceUser;
import mavmi.telegram_bot.common.service.IMenu;

public class ServiceUser extends AbsServiceUser {
    public ServiceUser(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName
    ) {
        super(userId, menu, username, firstName, lastName);
    }
}
