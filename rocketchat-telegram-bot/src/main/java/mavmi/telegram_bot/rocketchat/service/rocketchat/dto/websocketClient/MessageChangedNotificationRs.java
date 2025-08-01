package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.messageChangedNotification.MessageChangedNotificationError;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.messageChangedNotification.MessageChangedNotificationFields;

import java.util.List;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageChangedNotificationRs {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("collection")
    private String collection;
    @JsonProperty("id")
    private String id;
    @JsonProperty("methods")
    private List<String> methods;
    @JsonProperty("fields")
    private MessageChangedNotificationFields fields;
    @JsonProperty("error")
    private MessageChangedNotificationError error;
}
