package mavmi.telegram_bot.hb.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.HB_SERVICE_TASK;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

@Getter
@Setter
@Builder
public class HbServiceResponse extends ServiceResponse {
    private HB_SERVICE_TASK hbServiceTask;
    private MessageJson messageJson;
    private ReplyKeyboardJson replyKeyboardJson;
}
