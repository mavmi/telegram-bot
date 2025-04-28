package mavmi.telegram_bot.water_stuff.service.dto.waterStuffService;

import lombok.*;
import mavmi.telegram_bot.lib.dto.service.common.InlineKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.ReplyKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.common.UpdateMessageJson;
import mavmi.telegram_bot.lib.dto.service.common.tasks.WATER_STUFF_SERVICE_TASK;
import mavmi.telegram_bot.lib.dto.service.service.ServiceResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffServiceRs extends ServiceResponse {
    private WATER_STUFF_SERVICE_TASK waterStuffServiceTask;
    private MessageJson messageJson;
    private UpdateMessageJson updateMessageJson;
    private ReplyKeyboardJson replyKeyboardJson;
    private InlineKeyboardJson inlineKeyboardJson;
}
