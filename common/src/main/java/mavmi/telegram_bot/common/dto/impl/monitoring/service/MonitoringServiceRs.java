package mavmi.telegram_bot.common.dto.impl.monitoring.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.MONITORING_SERVICE_TASK;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringServiceRs implements IRs {
    @JsonProperty("task")
    private MONITORING_SERVICE_TASK monitoringServiceTask;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("keyboard")
    private KeyboardJson keyboardJson;
}
