package mavmi.telegram_bot.shakal_bot.service;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.AbsServiceUser;
import mavmi.telegram_bot.common.service.IMenu;

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
