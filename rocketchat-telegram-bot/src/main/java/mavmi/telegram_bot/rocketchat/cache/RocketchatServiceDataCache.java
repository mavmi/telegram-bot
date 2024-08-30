package mavmi.telegram_bot.rocketchat.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.RocketchatServiceDataCacheCommand;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.RocketchatServiceDataCacheCreds;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.RocketchatServiceDataCacheMessagesIdsHistory;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RocketchatServiceDataCache extends DataCache {

    private final RocketchatServiceDataCacheCreds creds = new RocketchatServiceDataCacheCreds();
    private final RocketchatServiceDataCacheMessagesIdsHistory messagesIdsHistory = new RocketchatServiceDataCacheMessagesIdsHistory();
    private final Map<String, RocketchatServiceDataCacheCommand> lastExecutedCommandNameToCommand = new HashMap<>();

    private String rocketchatUsername;
    private String rocketchatPasswordHash;
    private String rocketchatToken;
    private Long rocketchatTokenExpiryDate;
    private Long activeCommandHash;

    public RocketchatServiceDataCache(Long userId) {
        super(userId, RocketchatServiceMenu.MAIN_MENU);
    }

    public void putCommand(String commandName, long timestampMillis) {
        RocketchatServiceDataCacheCommand rocketchatServiceDataCacheCommand = lastExecutedCommandNameToCommand.get(commandName);

        if (rocketchatServiceDataCacheCommand == null) {
            rocketchatServiceDataCacheCommand = new RocketchatServiceDataCacheCommand(commandName, timestampMillis);
            lastExecutedCommandNameToCommand.put(commandName, rocketchatServiceDataCacheCommand);
        } else {
            rocketchatServiceDataCacheCommand.setTimestampMillis(timestampMillis);
        }
    }

    @Nullable
    public RocketchatServiceDataCacheCommand getCommandByName(String commandName) {
        return lastExecutedCommandNameToCommand.get(commandName);
    }
}
