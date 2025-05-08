package mavmi.telegram_bot.water_stuff.service.reminder.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;

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
