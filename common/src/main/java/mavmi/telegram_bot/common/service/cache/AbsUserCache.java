package mavmi.telegram_bot.common.service.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.IMenu;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbsUserCache {

    protected Long userId;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected List<String> lastMessages;
    @Setter
    protected IMenu menu;

    public AbsUserCache(
            Long userId,
            String username,
            String firstName,
            String lastName
    ) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastMessages = new ArrayList<>();
    }

    public AbsUserCache(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName
    ) {
        this(userId, username, firstName, lastName);
        this.menu = menu;
    }
}
