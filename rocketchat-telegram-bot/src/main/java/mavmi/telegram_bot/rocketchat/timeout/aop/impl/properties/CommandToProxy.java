package mavmi.telegram_bot.rocketchat.timeout.aop.impl.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandToProxy {
    private String name;
    private long timeoutSec;
}
