package mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.ReplyKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.common.tasks.MONITORING_SERVICE_TASK;
import mavmi.telegram_bot.lib.dto.service.service.ServiceResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringServiceRs extends ServiceResponse {
    @JsonProperty("task")
    private MONITORING_SERVICE_TASK monitoringServiceTask;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("reply_keyboard")
    private ReplyKeyboardJson replyKeyboardJson;
}
