package mavmi.telegram_bot.common.cache.api;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.common.cache.api.inner.MessagesContainer;
import mavmi.telegram_bot.common.service.menu.Menu;

/**
 * User's technical cache data
 */
@Getter
public abstract class DataCache {

    @Setter
    protected Long userId;
    @Setter
    protected String username;
    @Setter
    protected String firstName;
    @Setter
    protected String lastName;

    protected MenuContainer menuContainer;
    protected MessagesContainer messagesContainer;

    public DataCache(
            Long userId,
            Menu menu
    ) {
        this.menuContainer = new MenuContainer();
        this.messagesContainer = new MessagesContainer();
        this.userId = userId;

        this.menuContainer.add(menu);
    }

    public DataCache(
            Long userId,
            Menu menu,
            String username,
            String firstName,
            String lastName
    ) {
        this.menuContainer = new MenuContainer();
        this.messagesContainer = new MessagesContainer();
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;

        this.menuContainer.add(menu);
    }
}
