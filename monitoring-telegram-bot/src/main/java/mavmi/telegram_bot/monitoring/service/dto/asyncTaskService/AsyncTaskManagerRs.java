package mavmi.telegram_bot.monitoring.service.dto.asyncTaskService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.service.service.ServiceResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskManagerRs extends ServiceResponse {
    @JsonProperty("initiator_id")
    private Long initiatorId;
    @JsonProperty("message")
    private String message;
}
