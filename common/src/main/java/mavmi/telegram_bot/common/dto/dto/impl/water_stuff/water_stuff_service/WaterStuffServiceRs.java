package mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.common.InlineKeyboardJson;
import mavmi.telegram_bot.common.dto.common.UpdateMessageJson;
import mavmi.telegram_bot.common.dto.dto.api.Rs;
import mavmi.telegram_bot.common.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.WATER_STUFF_SERVICE_TASK;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffServiceRs implements Rs {
    @JsonProperty("task")
    private WATER_STUFF_SERVICE_TASK waterStuffServiceTask;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("update_message")
    private UpdateMessageJson updateMessageJson;
    @JsonProperty("reply_keyboard")
    private ReplyKeyboardJson replyKeyboardJson;
    @JsonProperty("inline_keyboard")
    private InlineKeyboardJson inlineKeyboardJson;
}
