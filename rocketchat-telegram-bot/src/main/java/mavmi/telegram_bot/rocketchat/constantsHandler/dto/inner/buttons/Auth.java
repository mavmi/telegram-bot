package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Auth {
    @JsonProperty("login_password")
    private String loginPassword;
    @JsonProperty("token")
    private String token;
}
