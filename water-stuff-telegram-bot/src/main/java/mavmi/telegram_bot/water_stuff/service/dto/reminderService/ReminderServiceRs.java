package mavmi.telegram_bot.water_stuff.service.dto.reminderService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.service.service.ServiceResponse;
import mavmi.telegram_bot.water_stuff.service.dto.reminderService.inner.ReminderServiceRsElement;

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
