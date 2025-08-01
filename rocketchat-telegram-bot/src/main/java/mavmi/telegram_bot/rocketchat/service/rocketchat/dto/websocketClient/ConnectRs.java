package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient;

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
public class ConnectRs {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("session")
    private String session;
}
