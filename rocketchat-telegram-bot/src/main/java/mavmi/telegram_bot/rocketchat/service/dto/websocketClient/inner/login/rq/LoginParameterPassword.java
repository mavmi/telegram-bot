package mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginParameterPassword {
    @JsonProperty("digest")
    private String digest;
    @JsonProperty("algorithm")
    private String algorithm;
}
