package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.messageChangedNotification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.lang.Nullable;

@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageChangedNotificationMdValue {
    @Getter
    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private JsonNode value;
    private String valueString;
    private MessageChangedNotificationMdValueValue valueObj;

    @Nullable
    public String getValueString() {
        if (value != null && value.isTextual()) {
            return value.asText();
        } else {
            return valueString;
        }
    }

    public MessageChangedNotificationMdValueValue getValueObj() {
        if (value != null && value.isObject()) {
            try {
                MessageChangedNotificationMdValueValue pp = new ObjectMapper().readValue(value.toPrettyString(), new TypeReference<MessageChangedNotificationMdValueValue>() {});
                return pp;
            } catch (JsonProcessingException e) {
                return valueObj;
            }
        } else {
            return valueObj;
        }
    }
}
