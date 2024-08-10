package mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResult {
    @JsonProperty("id")
    private String id;
    @JsonProperty("token")
    private String token;
    @JsonProperty("tokenExpires")
    private LoginTokenExpires tokenExpires;
    @JsonProperty("type")
    private String type;
}
