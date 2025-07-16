package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.sendCommand.rs.SendCommandError;

import java.util.List;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SendCommandRs {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("id")
    private String id;
    @JsonProperty("methods")
    private List<String> methods;
    @JsonProperty("error")
    private SendCommandError error;
}
