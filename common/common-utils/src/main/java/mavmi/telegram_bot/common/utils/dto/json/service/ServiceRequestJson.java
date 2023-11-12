package mavmi.telegram_bot.common.utils.dto.json.service;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestJson {
    private Long chatId;
    private List<Long> chatIdx;
    private ServiceMessageJson serviceMessageJson;
    private ServiceFileJson serviceFileJson;
    private ServiceKeyboardJson serviceKeyboardJson;
}
