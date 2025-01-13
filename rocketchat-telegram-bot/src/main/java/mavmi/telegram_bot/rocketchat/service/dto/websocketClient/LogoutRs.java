package mavmi.telegram_bot.rocketchat.service.dto.websocketClient;

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
public class LogoutRs {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("id")
    private String id;
}
