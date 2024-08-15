package mavmi.telegram_bot.rocketchat.service.dto.rocketchatService;

import lombok.*;
import mavmi.telegram_bot.common.service.dto.common.ImageJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.service.dto.ServiceResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RocketchatServiceRs extends ServiceResponse {
    private ROCKETCHAT_SERVICE_TASK rocketchatServiceTask;
    private MessageJson messageJson;
    private ImageJson imageJson;
}
