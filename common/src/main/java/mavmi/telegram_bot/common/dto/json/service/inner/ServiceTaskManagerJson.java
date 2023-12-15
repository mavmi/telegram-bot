package mavmi.telegram_bot.common.dto.json.service.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTaskManagerJson {
    @JsonProperty("target")
    private String target;
    @JsonProperty("message")
    private String message;
}
