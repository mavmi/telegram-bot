package mavmi.telegram_bot.common.utils.dto.json.bot.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotTaskManagerJson {
    @JsonProperty("target")
    private String target;
    @JsonProperty("message")
    private String message;
}
