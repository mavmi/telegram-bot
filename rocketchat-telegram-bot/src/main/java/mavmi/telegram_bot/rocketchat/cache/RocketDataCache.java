package mavmi.telegram_bot.rocketchat.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Commands;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.MessagesIdsHistory;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.MessagesToDelete;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;

@Getter
@Setter
public class RocketDataCache extends DataCache {

    private final Creds creds = new Creds();
    private final MessagesIdsHistory messagesIdsHistory = new MessagesIdsHistory();
    private final Commands commands = new Commands();
    private final MessagesToDelete messagesToDelete = new MessagesToDelete();

    private Long activeCommandHash;

    public RocketDataCache(Long userId) {
        super(userId, RocketMenu.MAIN_MENU);
    }
}
