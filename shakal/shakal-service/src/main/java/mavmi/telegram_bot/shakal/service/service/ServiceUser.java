package mavmi.telegram_bot.shakal.service.service;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.utils.service.AbsServiceUser;
import mavmi.telegram_bot.common.utils.service.IMenu;

@Getter
@Setter
public class ServiceUser extends AbsServiceUser {
    private int userDice;
    private int botDice;

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
