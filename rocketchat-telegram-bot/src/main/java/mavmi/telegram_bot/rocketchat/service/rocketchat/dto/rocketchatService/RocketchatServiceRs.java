package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService;

import lombok.*;
import mavmi.telegram_bot.lib.dto.service.common.DeleteMessageJson;
import mavmi.telegram_bot.lib.dto.service.common.ImageJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.lib.dto.service.service.ServiceResponse;

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
