package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.login.rs;

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
public class LoginError {
    @JsonProperty("error")
    private int error;
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorType")
    private String errorType;
}
