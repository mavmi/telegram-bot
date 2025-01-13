package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.buttons.Auth;

@Getter
@Setter
public class Buttons {
    @JsonProperty("auth")
    private Auth auth;
}
