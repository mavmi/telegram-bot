package mavmi.telegram_bot.shakal.service.shakal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.lib.dto.service.common.DiceJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.ReplyKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.common.tasks.SHAKAL_SERVICE_TASK;
import mavmi.telegram_bot.lib.dto.service.service.ServiceResponse;

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
