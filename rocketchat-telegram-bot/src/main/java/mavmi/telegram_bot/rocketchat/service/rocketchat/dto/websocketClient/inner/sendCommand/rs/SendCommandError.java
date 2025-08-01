package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.sendCommand.rs;

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
public class SendCommandError {
    @JsonProperty("isClientSafe")
    private Boolean isClientSafe;
    @JsonProperty("error")
    private Integer error;
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorType")
    private String errorType;
}
