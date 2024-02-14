package mavmi.telegram_bot.common.dto.impl.task_manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRq;
import mavmi.telegram_bot.common.dto.common.TaskManagerJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskManagerRq implements IRq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("task_manager_json")
    private TaskManagerJson taskManagerJson;
}
