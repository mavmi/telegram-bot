package mavmi.telegram_bot.lib.dto.service.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskManagerJson {
    @JsonProperty("target")
    private String target;
    @JsonProperty("message")
    private String message;
}
