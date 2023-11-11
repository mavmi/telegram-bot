package mavmi.telegram_bot.water_stuff.service.service;

import mavmi.telegram_bot.common.utils.service.AbsServiceUser;
import mavmi.telegram_bot.common.utils.service.IMenu;

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
