package mavmi.telegram_bot.water_stuff.constantsHandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.constants_handler.api.Constants;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.Requests;

/**
 * Container of bot's constants
 */
@Getter
@Setter
public class WaterConstants implements Constants {
    @JsonProperty("phrases")
    private Phrases phrases;
    @JsonProperty("requests")
    private Requests requests;
}
