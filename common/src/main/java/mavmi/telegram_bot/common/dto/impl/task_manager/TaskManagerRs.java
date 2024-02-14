package mavmi.telegram_bot.common.dto.impl.task_manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskManagerRs implements IRs {
    @JsonProperty("body")
    private String body;
}
