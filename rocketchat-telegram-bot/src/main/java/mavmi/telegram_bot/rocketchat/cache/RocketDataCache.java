package mavmi.telegram_bot.rocketchat.cache;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Command;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.MessagesIdsHistory;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.MessagesToDelete;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RocketDataCache extends DataCache {

    private final Creds creds = new Creds();
    private final MessagesIdsHistory messagesIdsHistory = new MessagesIdsHistory();
    private final Map<String, Command> lastExecutedCommandNameToCommand = new HashMap<>();
    private final MessagesToDelete msgsToDelete = new MessagesToDelete();

    private String rocketchatUsername;
    private String rocketchatPasswordHash;
    private String rocketchatToken;
    private Long rocketchatTokenExpiryDate;
    private Long activeCommandHash;

    public RocketDataCache(Long userId) {
        super(userId, RocketMenu.MAIN_MENU);
    }

    public void putCommand(String commandName, long timestampMillis) {
        Command command = lastExecutedCommandNameToCommand.get(commandName);

        if (command == null) {
            command = new Command(commandName, timestampMillis);
            lastExecutedCommandNameToCommand.put(commandName, command);
        } else {
            command.setTimestampMillis(timestampMillis);
        }
    }

    @Nullable
    public Command getCommandByName(String commandName) {
        return lastExecutedCommandNameToCommand.get(commandName);
    }
}
