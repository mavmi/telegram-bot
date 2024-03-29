package mavmi.telegram_bot.common.dto.impl.shakal.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.SHAKAL_SERVICE_TASK;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShakalServiceRs implements IRs {
    @JsonProperty("task")
    private SHAKAL_SERVICE_TASK shakalServiceTask;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("keyboard")
    private KeyboardJson keyboardJson;
    @JsonProperty("dice")
    private DiceJson diceJson;
}
