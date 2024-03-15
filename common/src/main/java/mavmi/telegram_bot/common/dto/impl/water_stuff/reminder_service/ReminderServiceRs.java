package mavmi.telegram_bot.common.dto.impl.water_stuff.reminder_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;
import mavmi.telegram_bot.common.dto.impl.water_stuff.reminder_service.inner.ReminderServiceRsElement;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderServiceRs implements IRs {
    @JsonProperty("messages")
    private List<ReminderServiceRsElement> reminderServiceRsElements;
}
