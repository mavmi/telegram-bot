package mavmi.telegram_bot.rocketchat.cache.inner.dataCache;

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
public class RocketchatServiceDataCacheCommand {
    private String command;
    private long timestampMillis;
}
