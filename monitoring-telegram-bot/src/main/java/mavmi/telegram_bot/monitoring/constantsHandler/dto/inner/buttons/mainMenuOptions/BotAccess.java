package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotAccess {
    @JsonProperty("bot_access")
    private String botAccess;
}
