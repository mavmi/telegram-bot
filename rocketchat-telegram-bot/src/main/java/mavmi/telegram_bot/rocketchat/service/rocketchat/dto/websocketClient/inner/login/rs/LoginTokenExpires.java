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
public class LoginTokenExpires {
    @JsonProperty("$date")
    private Long date;
}
