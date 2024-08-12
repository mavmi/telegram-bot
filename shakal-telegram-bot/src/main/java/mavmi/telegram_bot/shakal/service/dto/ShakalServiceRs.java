package mavmi.telegram_bot.shakal.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.service.dto.common.DiceJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.SHAKAL_SERVICE_TASK;
import mavmi.telegram_bot.common.service.service.ServiceResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShakalServiceRs extends ServiceResponse {
    @JsonProperty("task")
    private SHAKAL_SERVICE_TASK shakalServiceTask;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("reply_keyboard")
    private ReplyKeyboardJson replyKeyboardJson;
    @JsonProperty("dice")
    private DiceJson diceJson;
}
