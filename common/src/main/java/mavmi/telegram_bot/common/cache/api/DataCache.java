package mavmi.telegram_bot.common.cache.api;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.inner.MessagesContainer;
import mavmi.telegram_bot.common.service.menu.Menu;

/**
 * User's technical cache data
 */
@Getter
@Setter
public abstract class DataCache {

    protected Long userId;
    protected Menu menu;
    protected MessagesContainer messagesContainer = new MessagesContainer();

    public DataCache(
            Long userId,
            Menu menu
    ) {
        this.userId = userId;
        this.menu = menu;
    }
}
