package mavmi.telegram_bot.common.dto.dto.impl.monitoring.async_tasc_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskManagerRq implements Rq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("async_task_manager")
    private AsyncTaskManagerJson asyncTaskManagerJson;
}
