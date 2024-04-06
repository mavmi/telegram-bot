package mavmi.telegram_bot.common.dto.dto.impl.monitoring.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringServiceRq implements Rq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user")
    private UserJson userJson;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("file")
    private FileJson fileJson;
    @JsonProperty("async_task_manager")
    private AsyncTaskManagerJson asyncTaskManagerJson;
}
