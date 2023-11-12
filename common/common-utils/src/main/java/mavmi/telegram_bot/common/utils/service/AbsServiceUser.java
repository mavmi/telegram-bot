package mavmi.telegram_bot.common.utils.service;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbsServiceUser {
    protected Long userId;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected List<String> lastMessages;
    @Setter
    protected IMenu menu;

    public AbsServiceUser(
            Long userId,
            IMenu menu,
            String username,
            String firstName,
            String lastName
    ) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.menu = menu;
        this.lastMessages = new ArrayList<>();
    }
}
