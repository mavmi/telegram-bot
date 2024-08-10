package mavmi.telegram_bot.rocketchat.service.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rq.LoginParameter;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class LoginRq {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("method")
    private String method;
    @JsonProperty("id")
    private String id;
    @JsonProperty("params")
    private List<LoginParameter> params;
}
