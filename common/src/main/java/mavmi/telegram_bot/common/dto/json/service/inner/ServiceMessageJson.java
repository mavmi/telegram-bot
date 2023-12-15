package mavmi.telegram_bot.common.dto.json.service.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMessageJson {
    @JsonProperty("text_message")
    private String textMessage;
}
