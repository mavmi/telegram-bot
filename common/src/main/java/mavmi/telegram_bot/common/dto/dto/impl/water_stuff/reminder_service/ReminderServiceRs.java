package mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rs;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.inner.ReminderServiceRsElement;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderServiceRs implements Rs {
    @JsonProperty("messages")
    private List<ReminderServiceRsElement> reminderServiceRsElements;
}
