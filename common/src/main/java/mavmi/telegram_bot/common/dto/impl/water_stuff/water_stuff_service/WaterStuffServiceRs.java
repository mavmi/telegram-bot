package mavmi.telegram_bot.common.dto.impl.water_stuff.water_stuff_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.WATER_STUFF_SERVICE_TASK;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffServiceRs implements IRs {
    @JsonProperty("task")
    private WATER_STUFF_SERVICE_TASK waterStuffServiceTask;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("keyboard")
    private KeyboardJson keyboardJson;
}
