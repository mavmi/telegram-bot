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

    protected MenuContainer menuContainer = new MenuContainer();
    protected MessagesContainer messagesContainer = new MessagesContainer();

    public DataCache(
            Long userId,
            Menu menu
    ) {
        this.userId = userId;
        this.menuContainer.add(menu);
    }
}
