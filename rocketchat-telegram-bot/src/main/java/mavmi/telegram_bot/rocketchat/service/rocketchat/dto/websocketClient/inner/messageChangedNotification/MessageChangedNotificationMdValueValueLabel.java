package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.messageChangedNotification;

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
public class MessageChangedNotificationMdValueValueLabel {
    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private String value;
}
