package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases.BotAccess;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases.Common;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases.Privileges;

@Getter
@Setter
public class Phrases {
    @JsonProperty("common")
    private Common common;
    @JsonProperty("privileges")
    private Privileges privileges;
    @JsonProperty("bot-access")
    private BotAccess botAccess;
}
