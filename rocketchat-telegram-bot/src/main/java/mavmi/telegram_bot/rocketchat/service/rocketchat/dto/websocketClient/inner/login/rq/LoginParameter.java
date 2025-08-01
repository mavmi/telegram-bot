package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.login.rq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginParameter {
    @JsonProperty("user")
    private LoginParameterUser user;
    @JsonProperty("password")
    private LoginParameterPassword password;
    @JsonProperty("resume")
    private String resume;
}
