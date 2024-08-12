package mavmi.telegram_bot.monitoring.service.dto.monitoringService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.MONITORING_SERVICE_TASK;
import mavmi.telegram_bot.common.service.service.ServiceResponse;

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
