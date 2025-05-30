package mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    private String command;
    private long timestampMillis;
}
