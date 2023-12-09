package mavmi.telegram_bot.common.utils.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.utils.service.IMenu;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbsUserCache {

    protected Long userId;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected Boolean isPrivilegeGranted;
    protected List<String> lastMessages;
    @Setter
    protected IMenu menu;

    public AbsUserCache(
            Long userId,
            String username,
            String firstName,
            String lastName,
            Boolean isPrivilegeGranted
    ) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isPrivilegeGranted = isPrivilegeGranted;
        this.lastMessages = new ArrayList<>();
    }

    public AbsUserCache(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName,
            Boolean isPrivilegeGranted
    ) {
        this(userId, username, firstName, lastName, isPrivilegeGranted);
        this.menu = menu;
    }
}
