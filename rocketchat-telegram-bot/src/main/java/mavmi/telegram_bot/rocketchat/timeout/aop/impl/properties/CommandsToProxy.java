package mavmi.telegram_bot.rocketchat.timeout.aop.impl.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Component
@ConfigurationProperties(value = "service.commands")
public class CommandsToProxy {

    private List<CommandToProxy> commandsToProxy;

    @Nullable
    public CommandToProxy getCommandByName(String name) {
        return commandsToProxy
                .stream()
                .filter(command -> command.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
