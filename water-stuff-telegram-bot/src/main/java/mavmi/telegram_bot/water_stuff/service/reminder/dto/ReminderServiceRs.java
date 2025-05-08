package mavmi.telegram_bot.water_stuff.service.reminder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.lib.dto.service.service.ServiceResponse;
import mavmi.telegram_bot.water_stuff.service.reminder.dto.inner.ReminderServiceRsElement;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderServiceRs extends ServiceResponse {
    @JsonProperty("messages")
    private List<ReminderServiceRsElement> reminderServiceRsElements;
}
