package mavmi.telegram_bot.monitoring.asyncTaskService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTask {
    @JsonProperty("initiator_id")
    private Long initiatorId;
    @JsonProperty("target")
    private String target;
    @JsonProperty("message")
    private String message;
}
