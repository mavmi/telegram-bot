package mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginParameter {
    @JsonProperty("user")
    private LoginParameterUser user;
    @JsonProperty("password")
    private LoginParameterPassword password;
}
