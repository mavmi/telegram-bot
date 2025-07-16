package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.login.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginParameterUser {
    @JsonProperty("username")
    private String username;
}
