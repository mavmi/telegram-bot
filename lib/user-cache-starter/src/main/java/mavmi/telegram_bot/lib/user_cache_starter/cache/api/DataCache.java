package mavmi.telegram_bot.lib.user_cache_starter.cache.api;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.menu.container.MenuHistoryContainer;
import mavmi.telegram_bot.lib.user_cache_starter.message.container.MessageHistoryContainer;

/**
 * User's technical cache data
 */
@Getter
@Setter
public abstract class DataCache {

    protected Long userId;
    protected MenuHistoryContainer menuHistoryContainer = new MenuHistoryContainer();
    protected MessageHistoryContainer messagesContainer = new MessageHistoryContainer();

    public DataCache(Long userId,
            Menu menu) {
        this.userId = userId;
        this.menuHistoryContainer.add(menu);
    }
}
