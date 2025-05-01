package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotAccess {
    @JsonProperty("ask_for_user_id")
    private String askForUserId;
    @JsonProperty("invalid_id")
    private String invalidId;
}
