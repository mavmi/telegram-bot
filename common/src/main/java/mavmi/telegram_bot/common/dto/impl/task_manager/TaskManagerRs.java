package mavmi.telegram_bot.common.dto.impl.task_manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;
import mavmi.telegram_bot.common.taskManager.TASK_MANAGER_RQ_TYPE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskManagerRs implements IRs {
    @JsonProperty("body")
    private String body;
    @JsonProperty("type")
    private TASK_MANAGER_RQ_TYPE taskManagerRqType;
}
