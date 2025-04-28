package mavmi.telegram_bot.rocketchat.service.dto.rocketchatService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.UserJson;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RocketchatServiceRq extends ServiceRequest {
    private UserJson userJson;
    private MessageJson messageJson;
}
