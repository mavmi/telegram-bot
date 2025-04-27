package mavmi.telegram_bot.lib.user_cache_starter.cache.api;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.inner.MessagesContainer;

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
