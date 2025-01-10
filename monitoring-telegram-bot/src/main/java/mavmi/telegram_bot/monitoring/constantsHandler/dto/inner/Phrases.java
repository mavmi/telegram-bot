package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases.Common;

@Getter
@Setter
public class Phrases {
    @JsonProperty("common")
    private Common common;
}
