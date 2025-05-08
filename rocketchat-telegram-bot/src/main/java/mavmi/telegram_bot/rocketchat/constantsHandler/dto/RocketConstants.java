package mavmi.telegram_bot.rocketchat.constantsHandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.constants_handler.api.Constants;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Requests;

/**
 * Container of bot's constants
 */
@Getter
@Setter
public class RocketConstants implements Constants {
    @JsonProperty("requests")
    private Requests requests;
    @JsonProperty("phrases")
    private Phrases phrases;
}
