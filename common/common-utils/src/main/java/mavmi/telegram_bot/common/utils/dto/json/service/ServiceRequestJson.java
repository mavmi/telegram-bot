package mavmi.telegram_bot.common.utils.dto.json.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.utils.dto.json.IRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceFileJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceKeyboardJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceTaskManagerJson;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestJson implements IRequestJson {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("chat_idx")
    private List<Long> chatIdx;
    @JsonProperty("service_message_json")
    private ServiceMessageJson serviceMessageJson;
    @JsonProperty("service_file_json")
    private ServiceFileJson serviceFileJson;
    @JsonProperty("service_keyboard_json")
    private ServiceKeyboardJson serviceKeyboardJson;
    @JsonProperty("service_task_manager_json")
    private ServiceTaskManagerJson serviceTaskManagerJson;
}
