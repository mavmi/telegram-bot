package mavmi.telegram_bot.monitoring.service.monitoring.dto.asyncTaskService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mavmi.telegram_bot.lib.dto.service.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskManagerRq extends ServiceRequest {
    @JsonProperty("async_task_manager")
    private AsyncTaskManagerJson asyncTaskManagerJson;
}
