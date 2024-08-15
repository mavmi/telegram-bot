package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.buttons.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthButtons {
    @JsonProperty("login_password")
    private String loginPassword;
    @JsonProperty("token")
    private String token;
}
