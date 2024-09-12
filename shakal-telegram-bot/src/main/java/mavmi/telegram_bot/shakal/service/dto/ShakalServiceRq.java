package mavmi.telegram_bot.shakal.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mavmi.telegram_bot.common.service.dto.common.DiceJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.UserJson;
import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShakalServiceRq extends ServiceRequest {
    private UserJson userJson;
    private MessageJson messageJson;
    private DiceJson diceJson;
}
