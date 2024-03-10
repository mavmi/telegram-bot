package mavmi.telegram_bot.common.cache.userData;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.menu.IMenu;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AbstractUserDataCache {

    protected final List<String> messagesHistory = new ArrayList<>();

    protected Long userId;
    protected IMenu menu;
    protected String username;
    protected String firstName;
    protected String lastName;

    public AbstractUserDataCache(
            Long userId,
            IMenu menu
    ) {
        this.userId = userId;
        this.menu = menu;
    }

    public AbstractUserDataCache(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName
    ) {
        this.userId = userId;
        this.menu = menu;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
