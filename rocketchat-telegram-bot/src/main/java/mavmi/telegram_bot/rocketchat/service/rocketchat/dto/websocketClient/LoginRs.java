package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.login.rs.LoginError;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.login.rs.LoginResult;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRs {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("id")
    private String id;
    @JsonProperty("result")
    private LoginResult result;
    @JsonProperty("error")
    private LoginError error;
}
