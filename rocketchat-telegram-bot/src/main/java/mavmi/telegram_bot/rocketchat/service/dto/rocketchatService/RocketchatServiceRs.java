package mavmi.telegram_bot.rocketchat.service.dto.rocketchatService;

import lombok.*;
import mavmi.telegram_bot.common.service.dto.common.DeleteMessageJson;
import mavmi.telegram_bot.common.service.dto.common.ImageJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.dto.service.ServiceResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RocketchatServiceRs extends ServiceResponse {
    private List<ROCKETCHAT_SERVICE_TASK> rocketchatServiceTasks;
    private MessageJson messageJson;
    private ImageJson imageJson;
    private DeleteMessageJson deleteMessageJson;
}
