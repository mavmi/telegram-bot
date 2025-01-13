package mavmi.telegram_bot.rocketchat.cache.inner.dataCache;

import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.command.Command;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Commands {

    private final Map<String, Command> lastExecutedCommandNameToCommand = new HashMap<>();

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
