package mavmi.telegram_bot.rocketchat.constantsHandler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.constantsHandler.api.Constants;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Buttons;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Requests;

@Getter
@Setter
public class RocketchatServiceConstants implements Constants {
    @JsonProperty("buttons")
    private Buttons buttons;
    @JsonProperty("requests")
    private Requests requests;
    @JsonProperty("phrases")
    private Phrases phrases;
}
