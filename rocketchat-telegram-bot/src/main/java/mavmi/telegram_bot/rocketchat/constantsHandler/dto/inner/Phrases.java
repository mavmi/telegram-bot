package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases.Auth;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases.Common;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases.Qr;

@Getter
@Setter
public class Phrases {
    @JsonProperty("common")
    private Common common;
    @JsonProperty("auth")
    private Auth auth;
    @JsonProperty("qr")
    private Qr qr;
}
