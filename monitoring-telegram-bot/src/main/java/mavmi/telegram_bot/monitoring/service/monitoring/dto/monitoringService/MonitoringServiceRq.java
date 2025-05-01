package mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mavmi.telegram_bot.lib.dto.service.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.lib.dto.service.common.FileJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.UserJson;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringServiceRq extends ServiceRequest {
    @JsonProperty("user")
    private UserJson userJson;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("file")
    private FileJson fileJson;
    @JsonProperty("async_task_manager")
    private AsyncTaskManagerJson asyncTaskManagerJson;
}
