package mavmi.telegram_bot.monitoring.cache.inner.dataCache;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BotAccessManagement {
    private long workingTelegramId;
    private boolean waterStuff;
    private boolean monitoring;
}
