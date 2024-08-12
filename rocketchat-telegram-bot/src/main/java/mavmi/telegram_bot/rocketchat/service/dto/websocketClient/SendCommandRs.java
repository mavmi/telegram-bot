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
public class SendCommandRs {
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("error")
    private String error;
}
