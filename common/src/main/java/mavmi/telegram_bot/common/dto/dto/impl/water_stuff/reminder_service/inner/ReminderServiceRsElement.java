package mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.common.MessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderServiceRsElement {
    @JsonProperty("chat_id")
    private long chatId;
    @JsonProperty("message")
    private MessageJson messageJson;
}
