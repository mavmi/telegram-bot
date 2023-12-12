package mavmi.telegram_bot.common.utils.dto.json.service;

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
    private Long chatId;
    private List<Long> chatIdx;
    private ServiceMessageJson serviceMessageJson;
    private ServiceFileJson serviceFileJson;
    private ServiceKeyboardJson serviceKeyboardJson;
    private ServiceTaskManagerJson serviceTaskManagerJson;
}
